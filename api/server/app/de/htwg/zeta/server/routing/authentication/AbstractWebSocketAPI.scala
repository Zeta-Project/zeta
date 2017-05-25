package de.htwg.zeta.server.routing.authentication

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


  implicit class ReqToFutureOfBothRefToPropsAndTrans[IN, OUT](val func: (REQ) => Future[((ActorRef) => Props, MessageFlowTransformer[IN, OUT])]) // scalastyle:ignore

  def apply[IN, OUT](func: ReqToFutureOfBothRefToPropsAndTrans[IN, OUT]): WebSocket = buildWebSocket(toResultTuple[IN, OUT](func.func))

  implicit class ReqToFutureOfRefToPropsAndTrans[IN, OUT](val func: (REQ) => (Future[(ActorRef) => Props], Future[MessageFlowTransformer[IN, OUT]])) // scalastyle:ignore

  def apply[IN, OUT](cont: ReqToFutureOfRefToPropsAndTrans[IN, OUT]): WebSocket = {
    val propsAndTrans = (r: REQ) => {
      val t = cont.func(r)
      t._1.flatMap(props => t._2.map(trans => (props, trans))(system.dispatcher))(system.dispatcher)
    }
    buildWebSocket(toResultTuple[IN, OUT](propsAndTrans))
  }

  implicit class ReqToFutureOfRefToProps[IN, OUT](val func: (REQ) => (Future[(ActorRef) => Props], MessageFlowTransformer[IN, OUT])) // scalastyle:ignore

  def apply[IN, OUT](cont: ReqToFutureOfRefToProps[IN, OUT]): WebSocket = {
    val propsAndTrans: (REQ) => Future[((ActorRef) => Props, MessageFlowTransformer[IN, OUT])] = (r: REQ) => {
      val t = cont.func(r)
      t._1.map(props => (props, t._2))(system.dispatcher)
    }
    buildWebSocket(toResultTuple[IN, OUT](propsAndTrans))
  }

  implicit class RefAndReqToProps(val func: (ActorRef, REQ) => Props) // scalastyle:ignore

  def apply(getProps: RefAndReqToProps): WebSocket = buildWebSocket(toFutureFunction(getPropsAndTrans(getProps.func)))

  implicit class RefAndReqToPropsAndTrans[IN, OUT](val func: (ActorRef, REQ) => (Props, MessageFlowTransformer[IN, OUT])) // scalastyle:ignore

  def apply[IN, OUT](getProps: RefAndReqToPropsAndTrans[IN, OUT]): WebSocket = {
    val func = (req: REQ) => (out: ActorRef) => getProps.func(out, req)
    buildWebSocket(toFutureFunction(func))
  }

  implicit class RefToProps(val func: (ActorRef) => Props) // scalastyle:ignore

  def apply(getProps: RefToProps): WebSocket = apply((out: ActorRef, _: REQ) => getProps.func(out))

  implicit class ReqAndRefToPropsAndTrans[IN, OUT](val func: (REQ, ActorRef) => (Props, MessageFlowTransformer[IN, OUT])) // scalastyle:ignore

  def apply[IN, OUT](getProps: ReqAndRefToPropsAndTrans[IN, OUT]): WebSocket = {
    val func = (req: REQ) => (out: ActorRef) => getProps.func(req, out)
    buildWebSocket(toFutureFunction(func))
  }

  implicit class ReqAndRefToProps(val func: (REQ, ActorRef) => (Props)) // scalastyle:ignore

  def apply(getProps: ReqAndRefToProps): WebSocket = apply((out: ActorRef, req: REQ) => getProps.func(req, out))


}
