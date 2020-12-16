package de.htwg.zeta.server.controller

import javax.inject.Inject

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LogoutEvent
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.services.AuthenticatorResult
import de.htwg.zeta.server.silhouette.ZetaEnv
import play.api.mvc.AnyContent
import play.api.mvc.InjectedController
import play.api.mvc.Result

/**
 * The basic application controller.
 *
 * @param silhouette The Silhouette stack.
 */
class ApplicationController @Inject()(
    silhouette: Silhouette[ZetaEnv]
) extends InjectedController {

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
    val result = Ok
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, result)(request)
  }

}
