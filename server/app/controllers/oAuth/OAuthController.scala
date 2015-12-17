package controllers.oAuth

import models._
import models.oAuth.{OAuthDataHandler, OAuthSetup}
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{Action, Controller}

import scalaoauth2.provider.OAuth2ProviderActionBuilders._
import scalaoauth2.provider._

class OAuthController extends Controller with OAuth2Provider {

  implicit val authInfoWrites = new Writes[AuthInfo[SecureSocialUser]] {
    def writes(authInfo: AuthInfo[SecureSocialUser]) = {
      Json.obj(
        "account" -> Json.obj(
          "id" -> authInfo.user.profile.userId,
          "email" -> authInfo.user.profile.email.getOrElse[String]("no email address")
        ),
        "clientId" -> authInfo.clientId,
        "redirectUri" -> authInfo.redirectUri
      )
    }
  }

  def accessToken = Action.async { implicit request =>
    issueAccessToken(OAuthDataHandler())
  }

  // for debugging purposes
  def info = AuthorizedAction(OAuthDataHandler()) { request =>
    Ok(Json.toJson(request.authInfo))
  }

  // for debugging purposes
  def setup = Action {
    OAuthSetup.resetMongo()
    Ok("reset")
  }
}