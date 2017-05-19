package de.htwg.zeta.server.controller

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LogoutEvent
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import controllers.WebJarAssets
import controllers.routes
import de.htwg.zeta.server.util.auth.ZetaEnv
import models.User
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.mvc.Controller

/**
 * The basic application controller.
 *
 * @param messagesApi The Play messages API.
 * @param silhouette The Silhouette stack.
 * @param socialProviderRegistry The social provider registry.
 * @param webJarAssets The webjar assets implementation.
 */
class ApplicationController @Inject() (
    val messagesApi: MessagesApi,
    silhouette: Silhouette[ZetaEnv],
    socialProviderRegistry: SocialProviderRegistry,
    implicit val webJarAssets: WebJarAssets)
  extends Controller with I18nSupport {

  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def index = silhouette.SecuredAction { implicit request =>
    Ok(views.html.webpage.WebpageIndex(Some(request.identity)))
  }

  /**
   * Get the user id of the logged in user
   *
   * @return The user id
   */
  def user = silhouette.SecuredAction { implicit request =>
    Ok(User.getUserId(request.identity.loginInfo))
  }

  /**
   * Handles the Sign Out action.
   *
   * @return The result to display.
   */
  def signOut = silhouette.SecuredAction.async { implicit request =>
    val result = Redirect(routes.ScalaRoutes.appIndex())
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }
}
