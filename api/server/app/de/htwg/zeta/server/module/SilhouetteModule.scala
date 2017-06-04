package de.htwg.zeta.server.module

import java.util.UUID

import scala.concurrent.Future

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.name.Named
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.api.EventBus
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.SilhouetteProvider
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.actions.SecuredErrorHandler
import com.mohiva.play.silhouette.api.actions.UnsecuredErrorHandler
import com.mohiva.play.silhouette.api.crypto.CookieSigner
import com.mohiva.play.silhouette.api.crypto.Crypter
import com.mohiva.play.silhouette.api.crypto.CrypterAuthenticatorEncoder
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.api.util.CacheLayer
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.api.util.FingerprintGenerator
import com.mohiva.play.silhouette.api.util.HTTPLayer
import com.mohiva.play.silhouette.api.util.IDGenerator
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.api.util.PlayHTTPLayer
import com.mohiva.play.silhouette.crypto.JcaCookieSigner
import com.mohiva.play.silhouette.crypto.JcaCookieSignerSettings
import com.mohiva.play.silhouette.crypto.JcaCrypter
import com.mohiva.play.silhouette.crypto.JcaCrypterSettings
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticatorSettings
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticatorService
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.providers.OAuth1Info
import com.mohiva.play.silhouette.impl.providers.OAuth2Info
import com.mohiva.play.silhouette.impl.providers.OpenIDInfo
import com.mohiva.play.silhouette.impl.util.DefaultFingerprintGenerator
import com.mohiva.play.silhouette.impl.util.PlayCacheLayer
import com.mohiva.play.silhouette.impl.util.SecureRandomIDGenerator
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.daos.InMemoryAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import de.htwg.zeta.persistence.general.Persistence
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import de.htwg.zeta.persistence.transient.TransientPasswordInfoPersistence
import de.htwg.zeta.server.util.auth.CustomSecuredErrorHandler
import de.htwg.zeta.server.util.auth.CustomUnsecuredErrorHandler
import de.htwg.zeta.server.util.auth.ZetaEnv
import models.User
import net.ceedubs.ficus.Ficus
import net.ceedubs.ficus.Ficus.toFicusConfig
import net.ceedubs.ficus.readers.ArbitraryTypeReader.arbitraryTypeValueReader
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient

/**
 * The Guice module which wires all Silhouette dependencies.
 */
class SilhouetteModule extends AbstractModule with ScalaModule {

  /**
   * Configures the module.
   */
  def configure(): Unit = {
    bind[Silhouette[ZetaEnv]].to[SilhouetteProvider[ZetaEnv]]
    bind[UnsecuredErrorHandler].to[CustomUnsecuredErrorHandler]
    bind[SecuredErrorHandler].to[CustomSecuredErrorHandler]
    bind[CacheLayer].to[PlayCacheLayer]
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[PasswordHasher].toInstance(new BCryptPasswordHasher)
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())

    // Replace this with the bindings to your concrete DAOs
    bind[DelegableAuthInfoDAO[PasswordInfo]].to[TransientPasswordInfoPersistence] // TODO move this into own Persistence Module
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
  def provideHTTPLayer(client: WSClient): HTTPLayer = {
    new PlayHTTPLayer(client)
  }

  /** Provides the UserIdentityService
   *
   * @param loginInfoPersistence The loginInfoPersistence
   * @param userPersistence      The userPersistence
   * @return UserIdentityService
   */
  @Provides
  def provideUserIdentityService(
      loginInfoPersistence: LoginInfoPersistence, // scalastyle:ignore
      userPersistence: Persistence[User]): IdentityService[User] = {
    new IdentityService[User] {
      override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
        val userId = loginInfoPersistence.read(loginInfo)
        val user = userId.flatMap { userId => userPersistence.read(userId) }
        user.map { user => Some(user)
        }.recover {
          case _ => None
        }
      }
    }
  }

  /**
   * Provides the Silhouette environment.
   *
   * @param userService          The user service implementation.
   * @param authenticatorService The authentication service implementation.
   * @param eventBus             The event bus instance.
   * @return The Silhouette environment.
   */
  @Provides
  def provideEnvironment(
      userService: IdentityService[User], // scalastyle:ignore
      authenticatorService: AuthenticatorService[CookieAuthenticator],
      eventBus: EventBus
  ): Environment[ZetaEnv] = {

    Environment[ZetaEnv](
      userService,
      authenticatorService,
      Seq(),
      eventBus
    )
  }

  /**
   * Provides the cookie signer for the OAuth1 token secret provider.
   *
   * @param configuration The Play configuration.
   * @return The cookie signer for the OAuth1 token secret provider.
   */
  @Provides
  @Named("oauth1-token-secret-cookie-signer")
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
  @Provides
  @Named("oauth1-token-secret-crypter")
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
  @Provides
  @Named("oauth2-state-cookie-signer")
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
  @Provides
  @Named("authenticator-cookie-signer")
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
  @Provides
  @Named("authenticator-crypter")
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
   * @param oauth1InfoDAO   The implementation of the delegable OAuth1 auth info DAO.
   * @param oauth2InfoDAO   The implementation of the delegable OAuth2 auth info DAO.
   * @param openIDInfoDAO   The implementation of the delegable OpenID auth info DAO.
   * @return The auth info repository instance.
   */
  @Provides
  def provideAuthInfoRepository(
      passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo], // scalastyle:ignore
      oauth1InfoDAO: DelegableAuthInfoDAO[OAuth1Info],
      oauth2InfoDAO: DelegableAuthInfoDAO[OAuth2Info],
      openIDInfoDAO: DelegableAuthInfoDAO[OpenIDInfo]
  ): AuthInfoRepository = {

    new DelegableAuthInfoRepository(passwordInfoDAO, oauth1InfoDAO, oauth2InfoDAO, openIDInfoDAO)
  }

  /**
   * Provides the authenticator service.
   *
   * @param cookieSigner         The cookie signer implementation.
   * @param crypter              The crypter implementation.
   * @param fingerprintGenerator The fingerprint generator implementation.
   * @param idGenerator          The ID generator implementation.
   * @param configuration        The Play configuration.
   * @param clock                The clock instance.
   * @return The authenticator service.
   */
  @Provides
  def provideAuthenticatorService(
      @Named("authenticator-cookie-signer") cookieSigner: CookieSigner, // scalastyle:ignore
      @Named("authenticator-crypter") crypter: Crypter,
      fingerprintGenerator: FingerprintGenerator,
      idGenerator: IDGenerator,
      configuration: Configuration,
      clock: Clock
  ): AuthenticatorService[CookieAuthenticator] = {
    implicit val stringValueReader = Ficus.stringValueReader
    implicit val booleanValueReader = Ficus.booleanValueReader
    val config = configuration.underlying.as[CookieAuthenticatorSettings]("silhouette.authenticator")
    val encoder = new CrypterAuthenticatorEncoder(crypter)

    new CookieAuthenticatorService(config, None, cookieSigner, encoder, fingerprintGenerator, idGenerator, clock)
  }


  /**
   * Provides the password hasher registry.
   *
   * @param passwordHasher The default password hasher implementation.
   * @return The password hasher registry.
   */
  @Provides
  def providePasswordHasherRegistry(passwordHasher: PasswordHasher): PasswordHasherRegistry = {
    PasswordHasherRegistry(passwordHasher)
  }

  /**
   * Provides the credentials provider.
   *
   * @param authInfoRepository     The auth info repository implementation.
   * @param passwordHasherRegistry The password hasher registry.
   * @return The credentials provider.
   */
  @Provides
  def provideCredentialsProvider(
      authInfoRepository: AuthInfoRepository, // scalastyle:ignore
      passwordHasherRegistry: PasswordHasherRegistry
  ): CredentialsProvider = {

    new CredentialsProvider(authInfoRepository, passwordHasherRegistry)
  }

}
