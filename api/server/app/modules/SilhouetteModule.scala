package modules

import com.google.inject.name.Named
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.mohiva.play.silhouette.api.actions.SecuredErrorHandler
import com.mohiva.play.silhouette.api.actions.UnsecuredErrorHandler
import com.mohiva.play.silhouette.api.crypto.CookieSigner
import com.mohiva.play.silhouette.api.crypto.Crypter
import com.mohiva.play.silhouette.api.crypto.CrypterAuthenticatorEncoder
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.CacheLayer
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.api.util.FingerprintGenerator
import com.mohiva.play.silhouette.api.util.HTTPLayer
import com.mohiva.play.silhouette.api.util.IDGenerator
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.api.EventBus
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.SilhouetteProvider
import com.mohiva.play.silhouette.api.util.PlayHTTPLayer
import com.mohiva.play.silhouette.crypto.JcaCookieSigner
import com.mohiva.play.silhouette.crypto.JcaCookieSignerSettings
import com.mohiva.play.silhouette.crypto.JcaCrypter
import com.mohiva.play.silhouette.crypto.JcaCrypterSettings
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.providers.OAuth1Info
import com.mohiva.play.silhouette.impl.providers.OAuth1Settings
import com.mohiva.play.silhouette.impl.providers.OAuth1TokenSecretProvider
import com.mohiva.play.silhouette.impl.providers.OAuth2Info
import com.mohiva.play.silhouette.impl.providers.OAuth2Settings
import com.mohiva.play.silhouette.impl.providers.OAuth2StateProvider
import com.mohiva.play.silhouette.impl.providers.OpenIDInfo
import com.mohiva.play.silhouette.impl.providers.OpenIDSettings
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import com.mohiva.play.silhouette.impl.providers.oauth1.TwitterProvider
import com.mohiva.play.silhouette.impl.providers.oauth1.XingProvider
import com.mohiva.play.silhouette.impl.providers.oauth1.secrets.CookieSecretProvider
import com.mohiva.play.silhouette.impl.providers.oauth1.secrets.CookieSecretSettings
import com.mohiva.play.silhouette.impl.providers.oauth1.services.PlayOAuth1Service
import com.mohiva.play.silhouette.impl.providers.oauth2.ClefProvider
import com.mohiva.play.silhouette.impl.providers.oauth2.FacebookProvider
import com.mohiva.play.silhouette.impl.providers.oauth2.GoogleProvider
import com.mohiva.play.silhouette.impl.providers.oauth2.VKProvider
import com.mohiva.play.silhouette.impl.providers.oauth2.state.CookieStateProvider
import com.mohiva.play.silhouette.impl.providers.oauth2.state.CookieStateSettings
import com.mohiva.play.silhouette.impl.providers.oauth2.state.DummyStateProvider
import com.mohiva.play.silhouette.impl.providers.openid.YahooProvider
import com.mohiva.play.silhouette.impl.providers.openid.services.PlayOpenIDService
import com.mohiva.play.silhouette.impl.services.GravatarService
import com.mohiva.play.silhouette.impl.util.DefaultFingerprintGenerator
import com.mohiva.play.silhouette.impl.util.PlayCacheLayer
import com.mohiva.play.silhouette.impl.util.SecureRandomIDGenerator
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.daos.InMemoryAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import models.authenticators.SGCookieAuthenticator
import models.authenticators.SGCookieAuthenticatorService
import models.authenticators.SGCookieAuthenticatorSettings
import models.daos.SGAuthInfoDAO
import models.daos.SGUserDAO
import models.daos.UserDAO
import models.services.UserService
import models.services.UserServiceImpl
import models.session.Session
import net.ceedubs.ficus.Ficus
import net.ceedubs.ficus.Ficus.finiteDurationReader
import net.ceedubs.ficus.Ficus.mapValueReader
import net.ceedubs.ficus.Ficus.optionValueReader
import net.ceedubs.ficus.Ficus.toFicusConfig
import net.ceedubs.ficus.readers.ArbitraryTypeReader.arbitraryTypeValueReader
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.openid.OpenIdClient
import play.api.libs.ws.WSClient
import utils.auth.CustomSecuredErrorHandler
import utils.auth.CustomUnsecuredErrorHandler
import utils.auth.DefaultEnv

/**
 * The Guice module which wires all Silhouette dependencies.
 */
class SilhouetteModule extends AbstractModule with ScalaModule {

  /**
   * Configures the module.
   */
  def configure() {
    bind[Silhouette[DefaultEnv]].to[SilhouetteProvider[DefaultEnv]]
    bind[UnsecuredErrorHandler].to[CustomUnsecuredErrorHandler]
    bind[SecuredErrorHandler].to[CustomSecuredErrorHandler]
    bind[UserService].to[UserServiceImpl]
    bind[UserDAO].to[SGUserDAO]
    bind[CacheLayer].to[PlayCacheLayer]
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[PasswordHasher].toInstance(new BCryptPasswordHasher)
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())

    // Replace this with the bindings to your concrete DAOs
    bind[DelegableAuthInfoDAO[PasswordInfo]].to[SGAuthInfoDAO]
    bind[DelegableAuthInfoDAO[OAuth1Info]].toInstance(new InMemoryAuthInfoDAO[OAuth1Info])
    bind[DelegableAuthInfoDAO[OAuth2Info]].toInstance(new InMemoryAuthInfoDAO[OAuth2Info])
    bind[DelegableAuthInfoDAO[OpenIDInfo]].toInstance(new InMemoryAuthInfoDAO[OpenIDInfo])
  }

  /**
   * Provides the HTTP layer implementation.
   *
   * @param client Play's WS client.
   * @return The HTTP layer implementation.
   */
  @Provides
  def provideHTTPLayer(client: WSClient): HTTPLayer = new PlayHTTPLayer(client)

  /**
   * Provides the Silhouette environment.
   *
   * @param userService The user service implementation.
   * @param authenticatorService The authentication service implementation.
   * @param eventBus The event bus instance.
   * @return The Silhouette environment.
   */
  @Provides
  def provideEnvironment(
    userService: UserService,
    authenticatorService: AuthenticatorService[SGCookieAuthenticator],
    eventBus: EventBus
  ): Environment[DefaultEnv] = {

    Environment[DefaultEnv](
      userService,
      authenticatorService,
      Seq(),
      eventBus
    )
  }

  /**
   * Provides the social provider registry.
   *
   * @param facebookProvider The Facebook provider implementation.
   * @param googleProvider The Google provider implementation.
   * @param vkProvider The VK provider implementation.
   * @param clefProvider The Clef provider implementation.
   * @param twitterProvider The Twitter provider implementation.
   * @param xingProvider The Xing provider implementation.
   * @param yahooProvider The Yahoo provider implementation.
   * @return The Silhouette environment.
   */
  @Provides
  def provideSocialProviderRegistry(
    facebookProvider: FacebookProvider,
    googleProvider: GoogleProvider,
    vkProvider: VKProvider,
    clefProvider: ClefProvider,
    twitterProvider: TwitterProvider,
    xingProvider: XingProvider,
    yahooProvider: YahooProvider
  ): SocialProviderRegistry = {

    SocialProviderRegistry(Seq(
      googleProvider,
      facebookProvider,
      twitterProvider,
      vkProvider,
      xingProvider,
      yahooProvider,
      clefProvider
    ))
  }

  /**
   * Provides the cookie signer for the OAuth1 token secret provider.
   *
   * @param configuration The Play configuration.
   * @return The cookie signer for the OAuth1 token secret provider.
   */
  @Provides @Named("oauth1-token-secret-cookie-signer")
  def provideOAuth1TokenSecretCookieSigner(configuration: Configuration): CookieSigner = {
    implicit val stringValueReader = Ficus.stringValueReader
    implicit val booleanValueReader = Ficus.booleanValueReader
    val config = configuration.underlying.as[JcaCookieSignerSettings]("silhouette.oauth1TokenSecretProvider.cookie.signer")
    new JcaCookieSigner(config)
  }

  /**
   * Provides the crypter for the OAuth1 token secret provider.
   *
   * @param configuration The Play configuration.
   * @return The crypter for the OAuth1 token secret provider.
   */
  @Provides @Named("oauth1-token-secret-crypter")
  def provideOAuth1TokenSecretCrypter(configuration: Configuration): Crypter = {
    implicit val stringValueReader = Ficus.stringValueReader
    implicit val booleanValueReader = Ficus.booleanValueReader
    val config = configuration.underlying.as[JcaCrypterSettings]("silhouette.oauth1TokenSecretProvider.crypter")
    new JcaCrypter(config)
  }

  /**
   * Provides the cookie signer for the OAuth2 state provider.
   *
   * @param configuration The Play configuration.
   * @return The cookie signer for the OAuth2 state provider.
   */
  @Provides @Named("oauth2-state-cookie-signer")
  def provideOAuth2StageCookieSigner(configuration: Configuration): CookieSigner = {
    implicit val stringValueReader = Ficus.stringValueReader
    implicit val booleanValueReader = Ficus.booleanValueReader
    val config = configuration.underlying.as[JcaCookieSignerSettings]("silhouette.oauth2StateProvider.cookie.signer")
    new JcaCookieSigner(config)
  }

  /**
   * Provides the cookie signer for the authenticator.
   *
   * @param configuration The Play configuration.
   * @return The cookie signer for the authenticator.
   */
  @Provides @Named("authenticator-cookie-signer")
  def provideAuthenticatorCookieSigner(configuration: Configuration): CookieSigner = {
    implicit val stringValueReader = Ficus.stringValueReader
    implicit val booleanValueReader = Ficus.booleanValueReader
    val config = configuration.underlying.as[JcaCookieSignerSettings]("silhouette.authenticator.cookie.signer")
    new JcaCookieSigner(config)
  }

  /**
   * Provides the crypter for the authenticator.
   *
   * @param configuration The Play configuration.
   * @return The crypter for the authenticator.
   */
  @Provides @Named("authenticator-crypter")
  def provideAuthenticatorCrypter(configuration: Configuration): Crypter = {
    implicit val stringValueReader = Ficus.stringValueReader
    implicit val booleanValueReader = Ficus.booleanValueReader
    val config = configuration.underlying.as[JcaCrypterSettings]("silhouette.authenticator.crypter")
    new JcaCrypter(config)
  }

  /**
   * Provides the auth info repository.
   *
   * @param passwordInfoDAO The implementation of the delegable password auth info DAO.
   * @param oauth1InfoDAO The implementation of the delegable OAuth1 auth info DAO.
   * @param oauth2InfoDAO The implementation of the delegable OAuth2 auth info DAO.
   * @param openIDInfoDAO The implementation of the delegable OpenID auth info DAO.
   * @return The auth info repository instance.
   */
  @Provides
  def provideAuthInfoRepository(
    passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo],
    oauth1InfoDAO: DelegableAuthInfoDAO[OAuth1Info],
    oauth2InfoDAO: DelegableAuthInfoDAO[OAuth2Info],
    openIDInfoDAO: DelegableAuthInfoDAO[OpenIDInfo]
  ): AuthInfoRepository = {

    new DelegableAuthInfoRepository(passwordInfoDAO, oauth1InfoDAO, oauth2InfoDAO, openIDInfoDAO)
  }

  /**
   * Provides the authenticator service.
   *
   * @param cookieSigner The cookie signer implementation.
   * @param crypter The crypter implementation.
   * @param fingerprintGenerator The fingerprint generator implementation.
   * @param idGenerator The ID generator implementation.
   * @param configuration The Play configuration.
   * @param clock The clock instance.
   * @return The authenticator service.
   */
  @Provides
  def provideAuthenticatorService(
    @Named("authenticator-cookie-signer") cookieSigner: CookieSigner,
    @Named("authenticator-crypter") crypter: Crypter,
    fingerprintGenerator: FingerprintGenerator,
    idGenerator: IDGenerator,
    session: Session,
    configuration: Configuration,
    clock: Clock
  ): AuthenticatorService[SGCookieAuthenticator] = {
    implicit val stringValueReader = Ficus.stringValueReader
    implicit val booleanValueReader = Ficus.booleanValueReader
    val config = configuration.underlying.as[SGCookieAuthenticatorSettings]("silhouette.authenticator")
    val encoder = new CrypterAuthenticatorEncoder(crypter)

    new SGCookieAuthenticatorService(config, None, cookieSigner, encoder, fingerprintGenerator, idGenerator, clock, session)
  }

  /**
   * Provides the avatar service.
   *
   * @param httpLayer The HTTP layer implementation.
   * @return The avatar service implementation.
   */
  @Provides
  def provideAvatarService(httpLayer: HTTPLayer): AvatarService = new GravatarService(httpLayer)

  /**
   * Provides the OAuth1 token secret provider.
   *
   * @param cookieSigner The cookie signer implementation.
   * @param crypter The crypter implementation.
   * @param configuration The Play configuration.
   * @param clock The clock instance.
   * @return The OAuth1 token secret provider implementation.
   */
  @Provides
  def provideOAuth1TokenSecretProvider(
    @Named("oauth1-token-secret-cookie-signer") cookieSigner: CookieSigner,
    @Named("oauth1-token-secret-crypter") crypter: Crypter,
    configuration: Configuration,
    clock: Clock
  ): OAuth1TokenSecretProvider = {
    implicit val stringValueReader = Ficus.stringValueReader
    implicit val booleanValueReader = Ficus.booleanValueReader
    val settings = configuration.underlying.as[CookieSecretSettings]("silhouette.oauth1TokenSecretProvider")
    new CookieSecretProvider(settings, cookieSigner, crypter, clock)
  }

  /**
   * Provides the OAuth2 state provider.
   *
   * @param idGenerator The ID generator implementation.
   * @param cookieSigner The cookie signer implementation.
   * @param configuration The Play configuration.
   * @param clock The clock instance.
   * @return The OAuth2 state provider implementation.
   */
  @Provides
  def provideOAuth2StateProvider(
    idGenerator: IDGenerator,
    @Named("oauth2-state-cookie-signer") cookieSigner: CookieSigner,
    configuration: Configuration, clock: Clock
  ): OAuth2StateProvider = {
    implicit val stringValueReader = Ficus.stringValueReader
    implicit val booleanValueReader = Ficus.booleanValueReader
    val settings = configuration.underlying.as[CookieStateSettings]("silhouette.oauth2StateProvider")
    new CookieStateProvider(settings, idGenerator, cookieSigner, clock)
  }

  /**
   * Provides the password hasher registry.
   *
   * @param passwordHasher The default password hasher implementation.
   * @return The password hasher registry.
   */
  @Provides
  def providePasswordHasherRegistry(passwordHasher: PasswordHasher): PasswordHasherRegistry = {
    new PasswordHasherRegistry(passwordHasher)
  }

  /**
   * Provides the credentials provider.
   *
   * @param authInfoRepository The auth info repository implementation.
   * @param passwordHasherRegistry The password hasher registry.
   * @return The credentials provider.
   */
  @Provides
  def provideCredentialsProvider(
    authInfoRepository: AuthInfoRepository,
    passwordHasherRegistry: PasswordHasherRegistry
  ): CredentialsProvider = {

    new CredentialsProvider(authInfoRepository, passwordHasherRegistry)
  }

  /**
   * Provides the Facebook provider.
   *
   * @param httpLayer The HTTP layer implementation.
   * @param stateProvider The OAuth2 state provider implementation.
   * @param configuration The Play configuration.
   * @return The Facebook provider.
   */
  @Provides
  def provideFacebookProvider(
    httpLayer: HTTPLayer,
    stateProvider: OAuth2StateProvider,
    configuration: Configuration
  ): FacebookProvider = {
    implicit val stringValueReader = Ficus.stringValueReader
    implicit val booleanValueReader = Ficus.booleanValueReader
    new FacebookProvider(httpLayer, stateProvider, configuration.underlying.as[OAuth2Settings]("silhouette.facebook"))
  }

  /**
   * Provides the Google provider.
   *
   * @param httpLayer The HTTP layer implementation.
   * @param stateProvider The OAuth2 state provider implementation.
   * @param configuration The Play configuration.
   * @return The Google provider.
   */
  @Provides
  def provideGoogleProvider(
    httpLayer: HTTPLayer,
    stateProvider: OAuth2StateProvider,
    configuration: Configuration
  ): GoogleProvider = {
    implicit val stringValueReader = Ficus.stringValueReader
    implicit val booleanValueReader = Ficus.booleanValueReader
    new GoogleProvider(httpLayer, stateProvider, configuration.underlying.as[OAuth2Settings]("silhouette.google"))
  }

  /**
   * Provides the VK provider.
   *
   * @param httpLayer The HTTP layer implementation.
   * @param stateProvider The OAuth2 state provider implementation.
   * @param configuration The Play configuration.
   * @return The VK provider.
   */
  @Provides
  def provideVKProvider(
    httpLayer: HTTPLayer,
    stateProvider: OAuth2StateProvider,
    configuration: Configuration
  ): VKProvider = {
    implicit val stringValueReader = Ficus.stringValueReader
    implicit val booleanValueReader = Ficus.booleanValueReader
    new VKProvider(httpLayer, stateProvider, configuration.underlying.as[OAuth2Settings]("silhouette.vk"))
  }

  /**
   * Provides the Clef provider.
   *
   * @param httpLayer The HTTP layer implementation.
   * @param configuration The Play configuration.
   * @return The Clef provider.
   */
  @Provides
  def provideClefProvider(httpLayer: HTTPLayer, configuration: Configuration): ClefProvider = {
    implicit val stringValueReader = Ficus.stringValueReader
    implicit val booleanValueReader = Ficus.booleanValueReader
    new ClefProvider(httpLayer, new DummyStateProvider, configuration.underlying.as[OAuth2Settings]("silhouette.clef"))
  }

  /**
   * Provides the Twitter provider.
   *
   * @param httpLayer The HTTP layer implementation.
   * @param tokenSecretProvider The token secret provider implementation.
   * @param configuration The Play configuration.
   * @return The Twitter provider.
   */
  @Provides
  def provideTwitterProvider(
    httpLayer: HTTPLayer,
    tokenSecretProvider: OAuth1TokenSecretProvider,
    configuration: Configuration
  ): TwitterProvider = {
    implicit val stringValueReader = Ficus.stringValueReader
    implicit val booleanValueReader = Ficus.booleanValueReader
    val settings = configuration.underlying.as[OAuth1Settings]("silhouette.twitter")
    new TwitterProvider(httpLayer, new PlayOAuth1Service(settings), tokenSecretProvider, settings)
  }

  /**
   * Provides the Xing provider.
   *
   * @param httpLayer The HTTP layer implementation.
   * @param tokenSecretProvider The token secret provider implementation.
   * @param configuration The Play configuration.
   * @return The Xing provider.
   */
  @Provides
  def provideXingProvider(
    httpLayer: HTTPLayer,
    tokenSecretProvider: OAuth1TokenSecretProvider,
    configuration: Configuration
  ): XingProvider = {
    implicit val stringValueReader = Ficus.stringValueReader
    implicit val booleanValueReader = Ficus.booleanValueReader
    val settings = configuration.underlying.as[OAuth1Settings]("silhouette.xing")
    new XingProvider(httpLayer, new PlayOAuth1Service(settings), tokenSecretProvider, settings)
  }

  /**
   * Provides the Yahoo provider.
   *
   * @param cacheLayer The cache layer implementation.
   * @param httpLayer The HTTP layer implementation.
   * @param client The OpenID client implementation.
   * @param configuration The Play configuration.
   * @return The Yahoo provider.
   */
  @Provides
  def provideYahooProvider(
    cacheLayer: CacheLayer,
    httpLayer: HTTPLayer,
    client: OpenIdClient,
    configuration: Configuration
  ): YahooProvider = {
    implicit val stringValueReader = Ficus.stringValueReader
    implicit val booleanValueReader = Ficus.booleanValueReader
    val settings = configuration.underlying.as[OpenIDSettings]("silhouette.yahoo")
    new YahooProvider(httpLayer, new PlayOpenIDService(client, settings), settings)
  }
}
