package de.htwg.zeta.server.authentication

import scala.concurrent.Future

import akka.actor.Props
import akka.actor.ActorRef
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.WebSocket
import play.api.mvc.WebSocket.MessageFlowTransformer


/**
 */
trait AbstractWebSocketAPI[REQ <: Request[AnyContent]] extends AbstractWebSocket[REQ] {


  type MsgTrans[IN, OUT] = MessageFlowTransformer[IN, OUT]
  private type ReqToRefToPropsAndTrans[IN, OUT] = (REQ) => (ActorRef) => (Props, MsgTrans[IN, OUT])
  private type RegToFuture[F] = (REQ) => Future[F]


  private def toFutureFunction[T](func: REQ => T): REQ => Future[T] = (r: REQ) => Future(func(r))(system.dispatcher)


  private def getPropsAndTrans(getProps: (ActorRef, REQ) => Props): ReqToRefToPropsAndTrans[String, String] = {
    getPropsAndTrans(getProps, AbstractWebSocket.defaultTans)
  }

  private def getPropsAndTrans[IN, OUT](getProps: (ActorRef, REQ) => Props, trans: MsgTrans[IN, OUT]): ReqToRefToPropsAndTrans[IN, OUT] = {
    (req: REQ) => (out: ActorRef) => (getProps(out, req), trans)
  }

  private def toResultTuple[IN, OUT](func: RegToFuture[(ActorRef => Props, MsgTrans[IN, OUT])]): RegToFuture[ActorRef => (Props, MsgTrans[IN, OUT])] = {
    (req: REQ) =>
      func(req).map(tuple => {
        (ref: ActorRef) => (tuple._1(ref), tuple._2)
      })(system.dispatcher)
  }


  implicit class IF1[IN, OUT](val func: (REQ) => Future[((ActorRef) => Props, MessageFlowTransformer[IN, OUT])]) // scalastyle:ignore

  def apply[IN, OUT](func: IF1[IN, OUT]): WebSocket = buildWebSocket(toResultTuple[IN, OUT](func.func))

  implicit class IF2[IN, OUT](val func: (REQ) => (Future[(ActorRef) => Props], Future[MessageFlowTransformer[IN, OUT]])) // scalastyle:ignore

  def apply[IN, OUT](cont: IF2[IN, OUT]): WebSocket = {
    val propsAndTrans = (r: REQ) => {
      val t = cont.func(r)
      t._1.flatMap(props => t._2.map(trans => (props, trans))(system.dispatcher))(system.dispatcher)
    }
    buildWebSocket(toResultTuple[IN, OUT](propsAndTrans))
  }

  implicit class IF3[IN, OUT](val func: (REQ) => (Future[(ActorRef) => Props], MessageFlowTransformer[IN, OUT])) // scalastyle:ignore

  def apply[IN, OUT](cont: IF3[IN, OUT]): WebSocket = {
    val propsAndTrans: (REQ) => Future[((ActorRef) => Props, MessageFlowTransformer[IN, OUT])] = (r: REQ) => {
      val t = cont.func(r)
      t._1.map(props => (props, t._2))(system.dispatcher)
    }
    buildWebSocket(toResultTuple[IN, OUT](propsAndTrans))
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

  implicit class IF7[IN, OUT](val func: (REQ, ActorRef) => (Props, MessageFlowTransformer[IN, OUT])) // scalastyle:ignore

  def apply[IN, OUT](getProps: IF7[IN, OUT]): WebSocket = {
    val func = (req: REQ) => (out: ActorRef) => getProps.func(req, out)
    buildWebSocket(toFutureFunction(func))
  }

  implicit class IF8(val func: (REQ, ActorRef) => (Props)) // scalastyle:ignore

  def apply(getProps: IF8): WebSocket = apply((out: ActorRef, req: REQ) => getProps.func(req, out))


}
