package controllers

import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers
import models._
import play.api.Logger
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{Action, Controller}
import models.custom_context._

import scala.concurrent.Future
import scalaoauth2.provider._
import scalaoauth2.provider.OAuth2ProviderActionBuilders._

class OAuthController extends Controller with OAuth2Provider {

  RegisterJodaTimeConversionHelpers()

  implicit val authInfoWrites = new Writes[AuthInfo[SecureSocialUser]] {
    def writes(authInfo: AuthInfo[SecureSocialUser]) = {
      Json.obj(
        "account" -> Json.obj(
          "email" -> authInfo.user.profile.email.getOrElse[String]("invalid")
        ),
        "clientId" -> authInfo.clientId,
        "redirectUri" -> authInfo.redirectUri
      )
    }
  }

  def accessToken = Action.async { implicit request =>
    Logger.debug(s"#OAuthController - accessToken")
    issueAccessToken(new MyDataHandler())
  }

  def resources = AuthorizedAction({Logger.debug(s"#OAuthController - resources"); new MyDataHandler()}) { request =>
    Ok(Json.toJson(request.authInfo))
  }

  def resetMongo = Action {
    Setup.resetMongo()
    Ok("reset")
  }

  class MyDataHandler extends DataHandler[SecureSocialUser] {

    // common

    override def validateClient(clientCredential: ClientCredential, grantType: String): Future[Boolean] =  {
      Logger.debug(s" MyDataHandler - validateClient")
      Future.successful(OauthClient.validate(clientCredential.clientId, clientCredential.clientSecret.getOrElse(""), grantType))
    }

    override def getStoredAccessToken(authInfo: AuthInfo[SecureSocialUser]): Future[Option[AccessToken]] = {
      Logger.debug(s" MyDataHandler - getStoredAccessToken")
      Future.successful(OauthAccessToken.findByAuthorized(authInfo.user, authInfo.clientId.getOrElse("")).map(toAccessToken))
    }

    override def createAccessToken(authInfo: AuthInfo[SecureSocialUser]): Future[AccessToken] = {
      Logger.debug(s" MyDataHandler - createAccessToken")
      val clientId = authInfo.clientId.getOrElse(throw new InvalidClient())
      val oauthClient = OauthClient.findByClientId(clientId).getOrElse(throw new InvalidClient())
      val accessToken = OauthAccessToken.create(authInfo.user, oauthClient)
      Future.successful(toAccessToken(accessToken))
    }

    private val accessTokenExpireSeconds = 3600
    private def toAccessToken(accessToken: OauthAccessToken) = {
      Logger.debug(s" MyDataHandler - toAccessToken")
      AccessToken(
        accessToken.accessToken,
        Some(accessToken.refreshToken),
        None,
        Some(accessTokenExpireSeconds),
        accessToken.createdAt.toDate
      )
    }

    // Password grant

    override def findUser(username: String, password: String): Future[Option[SecureSocialUser]] = {
      Logger.debug(s" MyDataHandler - findUser")
      val x = MongoDbUserService.authenticate(username, password)
      Future.successful(x)
    }

    // Client credentials grant

    override def findClientUser(clientCredential: ClientCredential, scope: Option[String]): Future[Option[SecureSocialUser]] =  {
      Logger.debug(s" MyDataHandler - findClientUser")
      Future.successful(OauthClient.findClientCredentials(clientCredential.clientId, clientCredential.clientSecret.getOrElse("")))
    }

    // Refresh token grant

    override def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[SecureSocialUser]]] = {
      Logger.debug(s" MyDataHandler - findAuthInfoByRefreshToken")
      Future.successful(OauthAccessToken.findByRefreshToken(refreshToken).flatMap { accessToken =>
        for {
          account <- MongoDbUserService.findOneById(accessToken.accountId)
          client <- OauthClient.findOneById(accessToken.oauthClientId)
        } yield {
          AuthInfo(
            user = account,
            clientId = Some(client.clientId),
            scope = None,
            redirectUri = None
          )
        }
      })
    }

    override def refreshAccessToken(authInfo: AuthInfo[SecureSocialUser], refreshToken: String): Future[AccessToken] =  {
      Logger.debug(s" MyDataHandler - refreshAccessToken")
      val clientId = authInfo.clientId.getOrElse(throw new InvalidClient())
      val client = OauthClient.findByClientId(clientId).getOrElse(throw new InvalidClient())
      val accessToken = OauthAccessToken.refresh(authInfo.user, client)
      Future.successful(toAccessToken(accessToken))
    }

    // Authorization code grant

    override def findAuthInfoByCode(code: String): Future[Option[AuthInfo[SecureSocialUser]]] =  {
      Logger.debug(s" MyDataHandler - findAuthInfoByCode")
      Future.successful(OauthAuthorizationCode.findByCode(code).flatMap { authorization =>
        for {
          account <- MongoDbUserService.findOneById(authorization.accountId)
          client <- OauthClient.findOneById(authorization.oauthClientId)
        } yield {
          AuthInfo(
            user = account,
            clientId = Some(client.clientId),
            scope = None,
            redirectUri = authorization.redirectUri
          )
        }
      })
    }

    override def deleteAuthCode(code: String): Future[Unit] = {
      Logger.debug(s" MyDataHandler - deleteAuthCode")
      Future.successful(OauthAuthorizationCode.delete(code))
    }

    // Protected resource

    override def findAccessToken(token: String): Future[Option[AccessToken]] = {
      Logger.debug(s" MyDataHandler - findAccessToken")
      Future.successful(OauthAccessToken.findByAccessToken(token).map(toAccessToken))
    }

    override def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[SecureSocialUser]]] =  {
      Logger.debug(s" MyDataHandler - findAuthInfoByAccessToken")
      Future.successful(OauthAccessToken.findByAccessToken(accessToken.token).flatMap { case accessToken =>
        for {
          account <- MongoDbUserService.findOneById(accessToken.accountId)
          client <- OauthClient.findOneById(accessToken.oauthClientId)
        } yield {
          AuthInfo(
            user = account,
            clientId = Some(client.clientId),
            scope = None,
            redirectUri = None
          )
        }
      })
    }
  }
}