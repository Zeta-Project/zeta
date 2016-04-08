package controllers.oAuth

import javax.inject.Inject

import _root_.util.definitions.UserEnvironment
import models.SecureSocialUser
import models.oAuth.OAuthDataHandler
import play.api.data.Forms._
import play.api.data._
import play.api.libs.json._

import scala.concurrent.Future
import scalaoauth2.provider._

/**
  * This controller handles OAuth access token requests for users already authenticated
  * by SecureSocial. It is meant to be used with "local" modigen browser apps only. For anything else
  * (e.g. third party apps), OAuthController must be used instead.
  */
class OAuthLocalController @Inject()(override implicit val env: UserEnvironment) extends securesocial.core.SecureSocial {

  val handler = OAuthDataHandler()

  val oauthData = Form(
    tuple("grant_type" -> nonEmptyText, "client_id" -> nonEmptyText)
  )

  // a whitelist for modigen clients
  // => it's not possible to get access tokens for other (third party) clients using this controller
  val modigenClients = Set("modigen-browser-app1", "modigen-browserapp2")

  def accessToken = SecuredAction.async { implicit request =>
    oauthData.bindFromRequest.fold(
      error => Future.successful(BadRequest("missing params")),
      data => handleRequest(data, request.user)
    )
  }

  private def handleRequest(params: (String, String), user: SecureSocialUser) = {
    val (grantType, clientId) = params
    handler.validateClient(ClientCredential(clientId, Some("")), grantType) flatMap {
      case true if modigenClients contains clientId =>
        val authInfo = createAuthInfo(user, clientId)
        issueAccessToken(handler, authInfo).map {
          Ok(_)
        }
      case _ => Future.successful(BadRequest("invalid client"))
    }
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

  private def createAuthInfo(user: SecureSocialUser, clientId: String) = {
    AuthInfo(user, Some(clientId), None, None)
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