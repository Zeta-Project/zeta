package de.htwg.zeta.server.controller

import javax.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import grizzled.slf4j.Logging
import play.api.http.HttpVerbs
import play.api.i18n.Messages
import play.api.libs.ws.WSClient
import play.api.libs.ws.WSResponse
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.InjectedController
import play.api.mvc.Request
import play.api.mvc.Result


class WebAppController @Inject()(
    ws: WSClient,
    config: play.api.Configuration,
    implicit val ec: ExecutionContext,
) extends InjectedController with Logging {

  private val defaultHost = "localhost"
  private val defaultPort = 8080

  private lazy val urlPostfix: String = {
    val host = Option(config.get[String]("zeta.webapp.host")).getOrElse(defaultHost)
    val port = Option(config.get[Int]("zeta.webapp.port")).getOrElse(defaultPort).toString

    s"http://$host:$port"
  }

  /** Views the `Sign In` page.
   *
   * @param request  The request
   * @param messages The messages
   * @return The result to display.
   */
  def get(path: String)(request: Request[AnyContent], messages: Messages): Future[Result] = {
    executeRequest(urlPostfix + "/app/" + path)
  }

  private def executeRequest(url: String): Future[Result] = {
    ws.url(url).withMethod(HttpVerbs.GET).stream().map(processResponse(url))
  }

  private def processResponse(url: String)(response: WSResponse): Result = {
    if (response.status == OK) {
      val contentType = response.headers.get(CONTENT_TYPE).flatMap(_.headOption).getOrElse("application/octet-stream")
      Ok.chunked(response.bodyAsSource).as(contentType)
    } else {
      error(s"Requesting `$url` failed: ${response.status}")
      BadGateway
    }
  }

  def static(path: String): Action[AnyContent] = Action.async { implicit request =>
    executeRequest(urlPostfix + "/static/" + path)
  }
}
