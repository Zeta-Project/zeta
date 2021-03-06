package de.htwg.zeta.server.silhouette

import java.util.concurrent.TimeUnit

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext

import akka.util.Timeout
import com.google.inject.Provides
import com.google.inject.name.Named
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.api.EventBus
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.SilhouetteProvider
import com.mohiva.play.silhouette.api.actions.SecuredErrorHandler
import com.mohiva.play.silhouette.api.actions.UnsecuredErrorHandler
import com.mohiva.play.silhouette.api.crypto.Crypter
import com.mohiva.play.silhouette.api.crypto.CrypterAuthenticatorEncoder
import com.mohiva.play.silhouette.api.crypto.Signer
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
import com.mohiva.play.silhouette.api.util.PlayHTTPLayer
import com.mohiva.play.silhouette.crypto.JcaCrypter
import com.mohiva.play.silhouette.crypto.JcaCrypterSettings
import com.mohiva.play.silhouette.crypto.JcaSigner
import com.mohiva.play.silhouette.crypto.JcaSignerSettings
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticatorService
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticatorSettings
import com.mohiva.play.silhouette.impl.util.DefaultFingerprintGenerator
import com.mohiva.play.silhouette.impl.util.PlayCacheLayer
import com.mohiva.play.silhouette.impl.util.SecureRandomIDGenerator
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import com.typesafe.config.Config
import de.htwg.zeta.persistence.general.PasswordInfoRepository
import de.htwg.zeta.persistence.general.UserRepository
import de.htwg.zeta.server.actor.TokenCacheActor
import de.htwg.zeta.server.model.TokenCache
import net.ceedubs.ficus.Ficus
import net.ceedubs.ficus.Ficus.toFicusConfig
import net.ceedubs.ficus.readers.ArbitraryTypeReader.arbitraryTypeValueReader
import net.ceedubs.ficus.readers.ValueReader
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.libs.ws.WSClient
import play.api.mvc.Cookie.SameSite
import play.api.mvc.CookieHeaderEncoding
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * The Guice module which wires all Silhouette dependencies.
 */
class SilhouetteModule extends ScalaModule {

  /**
   * Configures the module.
   */
  override def configure(): Unit = {
    bind[Silhouette[ZetaEnv]].to[SilhouetteProvider[ZetaEnv]]
    bind[UnsecuredErrorHandler].to[CustomUnsecuredErrorHandler]
    bind[SecuredErrorHandler].to[CustomSecuredErrorHandler]
    bind[CacheLayer].to[PlayCacheLayer]
    bind[TokenCache].to[TokenCacheActor]
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[PasswordHasher].toInstance(new BCryptPasswordHasher)
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())
  }

  @Provides
  def getTimeoutTimer(): Timeout = Duration(1, TimeUnit.MINUTES)

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
   * @return UserIdentityService
   */
  @Provides
  def provideUserIdentityService(
      loginInfoPersistence: SilhouetteLoginInfoDao,
      userPersistence: UserRepository
  ): IdentityService[ZetaIdentity] = {
    new IdentityService[ZetaIdentity] {
      override def retrieve(loginInfo: LoginInfo): Future[Option[ZetaIdentity]] = {
        val futureIdentityOpt = for {
          // future
          userId <- loginInfoPersistence.read(loginInfo)
          user <- userPersistence.read(userId)
        } yield {
          Some(ZetaIdentity(user))
        }
        futureIdentityOpt.recover {
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
      userService: IdentityService[ZetaIdentity], // scalastyle:ignore
      authenticatorService: AuthenticatorService[CookieAuthenticator],
      eventBus: EventBus
  ): Environment[ZetaEnv] = {

    Environment[ZetaEnv](
      userService,
      authenticatorService,
      Seq.empty,
      eventBus
    )
  }

  /**
   * Provides the cookie signer for the authenticator.
   *
   * @param configuration The Play configuration.
   * @return The cookie signer for the authenticator.
   */
  @Provides
  @Named("authenticator-cookie-signer") // scalastyle:ignore multiple.string.literals
  def provideAuthenticatorCookieSigner(configuration: Configuration): Signer = {
    val config = {
      // required for parsing JcaCookieSignerSettings
      implicit val stringValueReader: ValueReader[String] = Ficus.stringValueReader
      implicit val booleanValueReader: ValueReader[Boolean] = Ficus.booleanValueReader

      implicit def optionReader[A](implicit valueReader: ValueReader[A]): ValueReader[Option[A]] = Ficus.optionValueReader[A](valueReader)

      configuration.underlying.as[JcaSignerSettings]("silhouette.authenticator.cookie.signer")
    }
    new JcaSigner(config)
  }

  /**
   * Provides the crypter for the authenticator.
   *
   * @param configuration The Play configuration.
   * @return The crypter for the authenticator.
   */
  @Provides
  @Named("authenticator-crypter") // scalastyle:ignore multiple.string.literals
  def provideAuthenticatorCrypter(configuration: Configuration): Crypter = {
    implicit val stringValueReader: ValueReader[String] = Ficus.stringValueReader
    implicit val booleanValueReader: ValueReader[Boolean] = Ficus.booleanValueReader
    val config = configuration.underlying.as[JcaCrypterSettings]("silhouette.authenticator.crypter")
    new JcaCrypter(config)
  }

  /**
   * Provides the auth info repository.
   *
   * @return The auth info repository instance.
   */
  @Provides
  def provideAuthInfoRepository(
      passwordInfoPersistence: PasswordInfoRepository
  ): AuthInfoRepository = {
    new DelegableAuthInfoRepository(new SilhouettePasswordInfoDao(passwordInfoPersistence))
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
      @Named("authenticator-cookie-signer") cookieSigner: Signer, // scalastyle:ignore
      @Named("authenticator-crypter") crypter: Crypter,
      fingerprintGenerator: FingerprintGenerator,
      idGenerator: IDGenerator,
      configuration: Configuration,
      cookieHeaderEncoding: CookieHeaderEncoding,
      clock: Clock
  ): AuthenticatorService[CookieAuthenticator] = {
    val config = {
      // required for parsing CookieAuthenticatorSettings
      implicit val stringValueReader: ValueReader[String] = Ficus.stringValueReader
      implicit val booleanValueReader: ValueReader[Boolean] = Ficus.booleanValueReader
      implicit val finiteDurationReader: ValueReader[FiniteDuration] = Ficus.finiteDurationReader

      implicit val sameSiteReader: ValueReader[SameSite] = new ValueReader[SameSite] {
        override def read(config: Config, path: String): SameSite = {
          val s = stringValueReader.read(config, path)
          SameSite.parse(s) match {
            case None => throw new IllegalArgumentException("Cannot parse SameSite")
            case Some(ss) => ss
          }
        }
      }

      implicit def optionReader[A](implicit valueReader: ValueReader[A]): ValueReader[Option[A]] =
        Ficus.optionValueReader[A](valueReader)


      configuration.underlying.as[CookieAuthenticatorSettings]("silhouette.authenticator")
    }
    val encoder = new CrypterAuthenticatorEncoder(crypter)

    new CookieAuthenticatorService(config, None, cookieSigner, cookieHeaderEncoding, encoder, fingerprintGenerator, idGenerator, clock)
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

}
