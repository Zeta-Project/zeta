package de.htwg.zeta.server.authentication

import scala.concurrent.Future
import scala.concurrent.Promise
import scala.util.Failure
import scala.util.Success

import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.ActorRef
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import com.mohiva.play.silhouette.api.HandlerResult
import com.mohiva.play.silhouette.api.Silhouette
import de.htwg.zeta.server.authentication
import de.htwg.zeta.server.util.auth.ZetaEnv
import grizzled.slf4j.Logging
import play.api.http.websocket.Message
import play.api.libs.streams.ActorFlow
import play.api.mvc.Controller
import play.api.mvc.AnyContent
import play.api.mvc.Result
import play.api.mvc.Request
import play.api.mvc.WebSocket
import play.api.mvc.AnyContentAsEmpty
import play.api.mvc.WebSocket.MessageFlowTransformer


trait AbstractWebSocket[REQ <: Request[AnyContent]] extends Controller with Logging {

  protected[authentication] val system: ActorSystem
  protected[authentication] val silhouette: Silhouette[ZetaEnv]
  protected[authentication] val mat: Materializer

  private def getPropsHandler[IN, OUT](futureProps: (REQ) => Future[(ActorRef) => (Props, MessageFlowTransformer[IN, OUT])])
    (req: REQ): Future[HandlerResult[(ActorRef) => (Props, MessageFlowTransformer[IN, OUT])]] = {
    val ret: Promise[HandlerResult[(ActorRef) => (Props, MessageFlowTransformer[IN, OUT])]] = Promise()
    futureProps(req).onComplete(t => {
      ret.success(t match {
        case Success((propsAndTrans)) =>
          HandlerResult(Ok, Some(propsAndTrans))
        case Failure(e) =>
          warn(e)
          HandlerResult(AbstractWebSocket.onFailure, None)
      })
    })(system.dispatcher)
    ret.future
  }

  protected[authentication] def handleRequest[T](request: Request[AnyContent])
    (buildFlow: REQ => Future[HandlerResult[T]]): Future[HandlerResult[T]]


  private def createFlow[IN, OUT](
    propsAndTrans: (ActorRef) => (Props, MessageFlowTransformer[IN, OUT])
  ): Future[Right[Result, Flow[Message, Message, _]]] = {
    val p = Promise[MessageFlowTransformer[IN, OUT]]()
    val func = (outRef: ActorRef) => {
      val ret = propsAndTrans(outRef)
      p.success(ret._2)
      ret._1
    }
    val flow = ActorFlow.actorRef[IN, OUT](out => func(out))(system, mat)
    p.future.map(trans => Right(trans.transform(flow)))(system.dispatcher)
  }


  private def getFlowEither[IN, OUT](
    request: Request[AnyContent],
    getProps: (REQ) => Future[(ActorRef) => (Props, MessageFlowTransformer[IN, OUT])]
  ): Future[Either[Result, Flow[Message, Message, _]]] = {
    handleRequest(request)(getPropsHandler[IN, OUT](getProps)).flatMap {
      case HandlerResult(_, Some(propsAndTrans)) =>
        createFlow(propsAndTrans)
      case HandlerResult(result, None) =>
        result match {
          case AbstractWebSocket.onFailure =>
          case any: Any => info(s"Caught internal result: $any")
        }
        Future.successful(Left(AbstractWebSocket.onFailure))
    }(system.dispatcher)
  }

  private def toFutureFunction[T](func: REQ => T): REQ => Future[T] = (r: REQ) => Future(func(r))(system.dispatcher)


  private def getPropsAndTrans(getProps: (ActorRef, REQ) => Props): (REQ) => (ActorRef) => (Props, MessageFlowTransformer[String, String]) = {
    getPropsAndTrans(getProps, AbstractWebSocket.defaultTans)
  }

  private def getPropsAndTrans[IN, OUT](
    getProps: (ActorRef, REQ) => Props, trans: MessageFlowTransformer[IN, OUT]
  ): (REQ) => (ActorRef) => (Props, MessageFlowTransformer[IN, OUT]) = {
    (req: REQ) => (out: ActorRef) => (getProps(out, req), trans)
  }


  private def buildWebSocket[IN, OUT](func: (REQ) => Future[(ActorRef) => (Props, MessageFlowTransformer[IN, OUT])]): WebSocket = {
    WebSocket(requestHeader => {
      val request = Request(requestHeader, AnyContentAsEmpty)
      getFlowEither(request, func)
    })
  }

  private def toPropsAndTransFunction[IN, OUT](
    func: (REQ) => Future[((ActorRef) => Props, MessageFlowTransformer[IN, OUT])]
  ): (REQ) => Future[(ActorRef) => (Props, MessageFlowTransformer[IN, OUT])] = {
    (req: REQ) =>
      func(req).map(tuple => {
        (ref: ActorRef) => (tuple._1(ref), tuple._2)
      })(system.dispatcher)
  }

  implicit class IF1[IN, OUT](val func: (REQ) => Future[((ActorRef) => Props, MessageFlowTransformer[IN, OUT])]) // scalastyle:ignore

  def apply[IN, OUT](func: IF1[IN, OUT]): WebSocket = buildWebSocket(toPropsAndTransFunction(func.func))

  implicit class IF2[IN, OUT](val func: (REQ) => (Future[(ActorRef) => Props], Future[MessageFlowTransformer[IN, OUT]])) // scalastyle:ignore

  def apply[IN, OUT](cont: IF2[IN, OUT]): WebSocket = {
    val propsAndTrans = (r: REQ) => {
      val t = cont.func(r)
      t._1.flatMap(props => t._2.map(trans => (props, trans))(system.dispatcher))(system.dispatcher)
    }
    buildWebSocket(toPropsAndTransFunction(propsAndTrans))
  }

  implicit class IF3[IN, OUT](val func: (REQ) => (Future[(ActorRef) => Props], MessageFlowTransformer[IN, OUT])) // scalastyle:ignore

  def apply[IN, OUT](cont: IF3[IN, OUT]): WebSocket = {
    val propsAndTrans: (REQ) => Future[((ActorRef) => Props, MessageFlowTransformer[IN, OUT])] = (r: REQ) => {
      val t = cont.func(r)
      t._1.map(props => (props, t._2))(system.dispatcher)
    }
    buildWebSocket(toPropsAndTransFunction(propsAndTrans))
  }

  implicit class IF4(val func: (ActorRef, REQ) => Props) // scalastyle:ignore

  def apply(getProps: IF4): WebSocket = buildWebSocket(toFutureFunction(getPropsAndTrans(getProps.func)))

  implicit class IF5[IN, OUT](val func: (ActorRef, REQ) => (Props, MessageFlowTransformer[IN, OUT])) // scalastyle:ignore

  def apply[IN, OUT](getProps: IF5[IN, OUT]): WebSocket = {
    val func = (req: REQ) => (out: ActorRef) => getProps.func(out, req)
    buildWebSocket(toFutureFunction(func))
  }

  implicit class IF6(val func: (ActorRef) => Props) // scalastyle:ignore

  def apply(getProps: IF6): WebSocket = apply((out: ActorRef, _: REQ) => getProps.func(out))

}


object AbstractWebSocket extends Controller {
  private[authentication] val defaultTans: MessageFlowTransformer[String, String] = MessageFlowTransformer.stringMessageFlowTransformer
  private[authentication] val onFailure: authentication.AbstractWebSocket.Status = InternalServerError
}
