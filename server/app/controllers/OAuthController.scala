package controllers

import models._
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future
import scalaoauth2.provider.OAuth2ProviderActionBuilders._
import scalaoauth2.provider._
import java.util.Date

class OAuthController extends Controller with OAuth2Provider {
/*
  implicit val authInfoWrites = new Writes[AuthInfo[SecureSocialUser]] {
    def writes(authInfo: AuthInfo[SecureSocialUser]) = {
      Json.obj(
        "account" -> Json.obj(
          "email" -> authInfo.user.profile.userId
        ),
        "clientId" -> authInfo.clientId,
        "redirectUri" -> authInfo.redirectUri
      )
    }
  }

  def accessToken = Action.async { implicit request =>
    issueAccessToken(new MyDataHandler())
  }

  def resources = AuthorizedAction(new MyDataHandler()) { request =>
    Ok(Json.toJson(request.authInfo))
  }

  class MyDataHandler extends DataHandler[SecureSocialUser] {

    // common

    override def validateClient(clientCredential: ClientCredential, grantType: String): Future[Boolean] = {
      Future.successful(
        MongoDbUserService.validate(clientCredential.clientId, clientCredential.clientSecret.getOrElse(""), grantType)
      )
    }

    override def getStoredAccessToken(authInfo: AuthInfo[SecureSocialUser]): Future[Option[AccessToken]] = {
      Future.successful(
        MongoDbUserService.findByAuthorized(authInfo.user, authInfo.clientId.getOrElse("")).map(toAccessToken)
      )
    }

    override def createAccessToken(authInfo: AuthInfo[SecureSocialUser]): Future[AccessToken] = {
      val clientId = authInfo.clientId.getOrElse(throw new InvalidClient())
      val oauthClient = MongoDbUserService.findByClientId(clientId).getOrElse(throw new InvalidClient())
      val accessToken = MongoDbUserService.create(authInfo.user, oauthClient)
      Future.successful(toAccessToken(accessToken))
    }

    private val accessTokenExpireSeconds = 3600

    private def toAccessToken(accessToken: Oauth_access_token) = {
      AccessToken(
        accessToken.access_token,
        Some(accessToken.refresh_token),
        None,
        Some(accessTokenExpireSeconds),
        new Date(accessToken.created_at)
      )
    }

    // Password grant

    override def findUser(username: String, password: String): Future[Option[SecureSocialUser]] = {
      Future.successful(
        MongoDbUserService.authenticate(username, password)
      )
    }

    // Client credentials grant

    override def findClientUser(clientCredential: ClientCredential, scope: Option[String]): Future[Option[SecureSocialUser]] = {
      Future.successful(MongoDbUserService.findClientCredentials(clientCredential.clientId, clientCredential.clientSecret.getOrElse("")))
    }

    // Refresh token grant

    override def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[SecureSocialUser]]] = {
      Future.successful(MongoDbUserService.findByRefreshToken(refreshToken).flatMap { accessToken =>
        for {
          account <- accessToken.account
          client <- accessToken.oauthClient
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

    override def refreshAccessToken(authInfo: AuthInfo[SecureSocialUser], refreshToken: String): Future[AccessToken] = {
      val clientId = authInfo.clientId.getOrElse(throw new InvalidClient())
      val client = MongoDbUserService.findByClientId(clientId).getOrElse(throw new InvalidClient())
      val accessToken = MongoDbUserService.refresh(authInfo.user, client)
      Future.successful(toAccessToken(accessToken))
    }

    // Authorization code grant

    override def findAuthInfoByCode(code: String): Future[Option[AuthInfo[SecureSocialUser]]] = {
      Future.successful(OauthAuthorizationCode.findByCode(code).flatMap { authorization =>
        for {
          account <- authorization.account
          client <- authorization.oauthClient
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
      Future.successful(
        OauthAuthorizationCode.delete(code)
      )
    }

    // Protected resource

    override def findAccessToken(token: String): Future[Option[AccessToken]] = {
      Future.successful(
        MongoDbUserService.findByAccessToken(token).map(toAccessToken)
      )
    }

    override def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[SecureSocialUser]]] = {
      Future.successful(
        MongoDbUserService.findByAccessToken(accessToken.token).flatMap { case accessToken =>
          for {
            account <- accessToken.account
            client <- accessToken.oauthClient
          } yield {
            AuthInfo(
              user = account,
              clientId = Some(client.clientId),
              scope = None,
              redirectUri = None
            )
          }
        }
      )
    }
  }
*/
}
