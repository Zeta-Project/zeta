package de.htwg.zeta.server.authentication

import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.ActorRef
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import com.mohiva.play.silhouette.api.HandlerResult
import com.mohiva.play.silhouette.api.Silhouette
import grizzled.slf4j.Logging
import play.api.libs.streams.ActorFlow
import play.api.mvc.Controller
import play.api.mvc.AnyContent
import play.api.mvc.Result
import play.api.mvc.Request
import play.api.mvc.WebSocket
import play.api.mvc.AnyContentAsEmpty
import de.htwg.zeta.server.utils.auth.ZetaEnv


trait AbstractWebSocket[REQ <: Request[AnyContent]] extends Controller with Logging {

  protected[authentication] val system: ActorSystem
  protected[authentication] val silhouette: Silhouette[ZetaEnv]
  protected[authentication] val mat: Materializer

  private def getFlowHandler(getProps: (ActorRef, REQ) => Props)(req: REQ): Future[HandlerResult[Flow[String, String, _]]] = Future {
    Try(ActorFlow.actorRef(out => getProps(out, req))(system, mat)) match {
      case Success(flow) =>
        HandlerResult(Ok, Some(flow))

      case Failure(e) =>
        warn(e)
        HandlerResult(AbstractWebSocket.onFailure, None)
    }
  }(system.dispatcher)

  protected[authentication] def handleRequest(request: Request[AnyContent])
    (buildFlow: REQ => Future[HandlerResult[Flow[String, String, _]]]): Future[HandlerResult[Flow[String, String, _]]]


  private def getFlowEither(request: Request[AnyContent], getProps: (ActorRef, REQ) => Props): Future[Either[Result, Flow[String, String, _]]] = {
    handleRequest(request)(getFlowHandler(getProps)).map {
      case HandlerResult(_, Some(flow)) => Right(flow)
      case HandlerResult(result, None) => result match {
        case AbstractWebSocket.onFailure =>
        case any: Any => info(s"silhouette returned result: $any")
      }
        Left(AbstractWebSocket.onFailure)
    }(system.dispatcher)
  }

  def apply(getProps: (ActorRef, REQ) => Props): WebSocket = {
    WebSocket.acceptOrResult[String, String](requestHeader => {
      val request = Request(requestHeader, AnyContentAsEmpty)
      getFlowEither(request, getProps)
    })
  }

  def apply(getProps: (ActorRef) => Props): WebSocket = apply((out, _) => getProps(out))

}


object AbstractWebSocket extends Controller {
  private[authentication] val onFailure = InternalServerError
}
