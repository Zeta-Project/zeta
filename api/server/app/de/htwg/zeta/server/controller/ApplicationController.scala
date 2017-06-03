package de.htwg.zeta.server.controller

import javax.inject.Inject

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LogoutEvent
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.services.AuthenticatorResult
import controllers.routes
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result

/**
 * The basic application controller.
 *
 * @param silhouette The Silhouette stack.
 */
class ApplicationController @Inject()(
    silhouette: Silhouette[ZetaEnv])
  extends Controller {

  /** Handles the index action.
   *
   * @param request The request
   * @return The result to display.
   */
  def index(request: SecuredRequest[ZetaEnv, AnyContent]): Result = {
    Ok(views.html.webpage.WebpageIndex(Some(request.identity)))
  }

  /** Get the user id of the logged in user
   *
   * @param request The request
   * @return The user id
   */
  def user(request: SecuredRequest[ZetaEnv, AnyContent]): Result = {
    Ok(request.identity.id.toString)
  }

  /** Handles the Sign Out action.
   *
   * @param request The request
   * @return The result to display.
   */
  def signOut(request: SecuredRequest[ZetaEnv, AnyContent]): Future[AuthenticatorResult] = {
    val result = Redirect(routes.ScalaRoutes.getIndex())
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, result)(request)
  }

}
