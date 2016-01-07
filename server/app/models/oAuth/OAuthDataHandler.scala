package models.oAuth

import models.{MongoDbUserService, SecureSocialUser}

import scala.concurrent.Future
import scalaoauth2.provider._

object OAuthDataHandler {
  private val handler = new OAuthDataHandler

  def apply() = handler
}

class OAuthDataHandler extends DataHandler[SecureSocialUser] {

  // common

  override def validateClient(clientCredential: ClientCredential, grantType: String): Future[Boolean] = {
    Future.successful(OauthClient.validate(clientCredential.clientId, clientCredential.clientSecret.getOrElse(""), grantType))
  }

  override def getStoredAccessToken(authInfo: AuthInfo[SecureSocialUser]): Future[Option[AccessToken]] = {
    Future.successful(OauthAccessToken.findByAuthorized(authInfo.user, authInfo.clientId.getOrElse("")).map(toAccessToken))
  }

  override def createAccessToken(authInfo: AuthInfo[SecureSocialUser]): Future[AccessToken] = {
    val clientId = authInfo.clientId.getOrElse(throw new InvalidClient())
    val oauthClient = OauthClient.findByClientId(clientId).getOrElse(throw new InvalidClient())
    val accessToken = OauthAccessToken.create(authInfo.user, oauthClient)
    Future.successful(toAccessToken(accessToken))
  }

  private val accessTokenExpireSeconds = 3600

  private def toAccessToken(accessToken: OauthAccessToken) = {
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
    val x = MongoDbUserService.authenticate(username, password)
    Future.successful(x)
  }

  // Client credentials grant

  override def findClientUser(clientCredential: ClientCredential, scope: Option[String]): Future[Option[SecureSocialUser]] = {
    Future.successful(OauthClient.findClientCredentials(clientCredential.clientId, clientCredential.clientSecret.getOrElse("")))
  }

  // Refresh token grant

  override def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[SecureSocialUser]]] = {
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

  override def refreshAccessToken(authInfo: AuthInfo[SecureSocialUser], refreshToken: String): Future[AccessToken] = {
    val clientId = authInfo.clientId.getOrElse(throw new InvalidClient())
    val client = OauthClient.findByClientId(clientId).getOrElse(throw new InvalidClient())
    val accessToken = OauthAccessToken.refresh(authInfo.user, client)
    Future.successful(toAccessToken(accessToken))
  }

  // Authorization code grant

  override def findAuthInfoByCode(code: String): Future[Option[AuthInfo[SecureSocialUser]]] = {
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
    Future.successful(OauthAuthorizationCode.delete(code))
  }

  // Protected resource

  override def findAccessToken(token: String): Future[Option[AccessToken]] = {
    Future.successful(OauthAccessToken.findByAccessToken(token).map(toAccessToken))
  }

  override def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[SecureSocialUser]]] = {
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