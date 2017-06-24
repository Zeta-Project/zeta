package de.htwg.zeta.server.controller.generatorControlForwader

import java.util.UUID
import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.ReceiveTimeout
import de.htwg.zeta.generatorControl.actors.frontendManager.FrontendManager
import grizzled.slf4j.Logging

/**
 */
class GeneratorControlForwarder(remoteClient: RemoteService, out: ActorRef, factory: GeneratorControlRegisterFactory) extends Actor with Logging {

  private val ident: UUID = UUID.randomUUID()
  private val outForwarder: ActorRef = context.actorOf(DirectForwarder.props(out))

  context.setReceiveTimeout(Duration(5, TimeUnit.MINUTES))

  send(factory(ident, outForwarder))

  private def send(msg: Any): Unit = {
    remoteClient.send(msg, self)
  }


  override def receive: Receive = {
    case ReceiveTimeout => send(FrontendManager.KeepHandlerAlive(ident))
    case msg: Any => send(FrontendManager.ForwardMessage(ident, msg))
  }

  override def postStop(): Unit = {
    send(FrontendManager.TerminateFrontend(ident))
  }

}

object GeneratorControlForwarder {
  def props(remoteClient: RemoteService, out: ActorRef, factory: GeneratorControlRegisterFactory): Props =
    Props(new GeneratorControlForwarder(remoteClient, out, factory))
}

trait GeneratorControlRegisterFactory {
  def apply(ident: UUID, out: ActorRef): FrontendManager.Create
}

object GeneratorControlRegisterFactory {

  private class GeneratorControlRegisterFactoryImpl(func: (UUID, ActorRef) => FrontendManager.Create) extends GeneratorControlRegisterFactory {
    override def apply(ident: UUID, out: ActorRef): FrontendManager.Create = func(ident, out)
  }

  def apply(func: (UUID, ActorRef) => FrontendManager.Create): GeneratorControlRegisterFactory = {
    new GeneratorControlRegisterFactoryImpl(func)
  }
}
