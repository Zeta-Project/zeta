package controllers

import models.SecureSocialUser
import models.oauth.OAuthDataHandler
import play.api.libs.json._
import securesocial.core.RuntimeEnvironment

import scala.concurrent.Future
import scalaoauth2.provider.{AccessToken, GrantHandlerResult, AuthInfo, AuthorizationHandler}

/**
  * This controller handles OAuth access token requests for users already authenticated
  * by SecureSocial. It is meant to be used with "local" modigen browser apps only. For anything else
  * (e.g. third party apps), OAuthController must be used instead.
  */
class OAuthLocalController(override implicit val env: RuntimeEnvironment[SecureSocialUser])
  extends securesocial.core.SecureSocial[SecureSocialUser] {

  //val modigenClients = Set("modigen-browser-app1", "modigen-browserapp2")

  def accessToken = SecuredAction.async { implicit request =>
    val authInfo = createAuthInfo(request.user)
    issueAccessToken(OAuthDataHandler(), authInfo).map { Ok(_) }
  }

  private def issueAccessToken(
    handler: AuthorizationHandler[SecureSocialUser],
    authInfo: AuthInfo[SecureSocialUser]
  ): Future[JsObject] = {
    handler.getStoredAccessToken(authInfo).flatMap {
      case Some(token) if token.isExpired => token.refreshToken.map {
        handler.refreshAccessToken(authInfo, _)
      }.getOrElse {
        handler.createAccessToken(authInfo)
      }
      case Some(token) => Future.successful(token)
      case None => handler.createAccessToken(authInfo)
    }.map(jsonResult)
  }

  private def createAuthInfo(user: SecureSocialUser) = {
    AuthInfo(user, Some("authClientId"), None, None)
  }

  private def jsonResult(accessToken: AccessToken) = {
    Json.obj(
      "token_type" -> "Bearer",
      "access_token" -> accessToken.token,
      "expires_in" -> accessToken.expiresIn,
      "refresh_token" -> accessToken.refreshToken,
      "scope" -> accessToken.scope)
  }

}