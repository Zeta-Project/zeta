package de.htwg.zeta.server.controller

import javax.inject.Inject

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import akka.stream.scaladsl.Source
import akka.util.ByteString
import play.api.http.HttpVerbs
import play.api.i18n.Messages
import play.api.libs.ws.StreamedResponse
import play.api.libs.ws.WSClient
import play.api.libs.ws.WSResponseHeaders
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.Result

/**
 * @author Philipp Daniels
 */
class WebAppController @Inject()(ws: WSClient, config: play.api.Configuration) extends Controller {

  private val defaultHost = "localhost"
  private val defaultPort = 8080

  private lazy val urlPostfix: String = {
    val host = config.getString("zeta.webapp.host").getOrElse(defaultHost).toString
    val port = config.getInt("zeta.webapp.port").getOrElse(defaultPort).toString

    s"http://$host:$port/"
  }

  private def getUrl(path: String): String = {
    urlPostfix + path
  }

  /** Views the `Sign In` page.
   *
   * @param request  The request
   * @param messages The messages
   * @return The result to display.
   */
  def get(path: String)(request: Request[AnyContent], messages: Messages): Future[Result] = {
    val url = getUrl(path)
    ws.url(url).withMethod(HttpVerbs.GET).stream().map {
      case StreamedResponse(response, body) => processGetResponse(response, body)
    }
  }

  private def processGetResponse(response: WSResponseHeaders, body: Source[ByteString, _]): Result = {
    if (response.status == OK) {
      val contentType = response.headers.get(CONTENT_TYPE).flatMap(_.headOption).getOrElse("application/octet-stream")
      Ok.chunked(body).as(contentType)
    } else {
      BadGateway
    }
  }
}
