package de.htwg.zeta.server.controller.generatorControlForwader

import java.util.UUID
import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration

import akka.actor.ActorPath
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.ReceiveTimeout
import akka.cluster.client.ClusterClient
import akka.cluster.client.ClusterClientSettings
import de.htwg.zeta.common.cluster.ClusterManager
import de.htwg.zeta.generatorControl.actors.frontendManager.FrontendManager
import grizzled.slf4j.Logging

/**
 * @param clusterAddress in format s"${ip}:${port}"
 */
class BackendForwarder(clusterAddress: String, serviceName: String, out: ActorRef, factory: BackendRegisterFactory) extends Actor with Logging {

  private val ident: UUID = UUID.randomUUID()
  private val outForwarder: ActorRef = context.actorOf(DirectForwarder.props(out))

  private val receptionistPath: String = s"${ClusterManager.clusterPathPrefix}$clusterAddress/system/receptionist"
  private val initialContacts: Set[ActorPath] = Set(ActorPath.fromString(receptionistPath))
  private val settings: ClusterClientSettings = ClusterClientSettings(context.system).withInitialContacts(initialContacts)
  //  private val client: ActorRef = context.system.actorOf(ClusterClient.props(settings), s"${serviceName}Client")
  private val client: ActorRef = context.system.actorOf(ClusterClient.props(settings))
  private val clientPath = s"/user/$serviceName"

  context.setReceiveTimeout(Duration(5, TimeUnit.MINUTES))

  send(factory(ident, outForwarder))

  private def send(msg: Any): Unit = {
    // setting localAffinity to true is unnecessary.
    client ! ClusterClient.Send(clientPath, msg, localAffinity = false)
  }

  override def receive: Receive = {
    case ReceiveTimeout => send(FrontendManager.KeepHandlerAlive(ident))
    case msg: Any => send(FrontendManager.ForwardMessage(ident, msg))
  }

  override def postStop(): Unit = {
    send(FrontendManager.TerminateFrontend(ident))
  }

}

object BackendForwarder {
  def props(clusterAddress: String, serviceName: String, out: ActorRef, factory: BackendRegisterFactory): Props =
    Props(new BackendForwarder(clusterAddress, serviceName, out, factory))
}

trait BackendRegisterFactory {
  def apply(ident: UUID, out: ActorRef): FrontendManager.Create
}

object BackendRegisterFactory {

  private class BackendRegisterFactoryImpl(func: (UUID, ActorRef) => FrontendManager.Create) extends BackendRegisterFactory {
    override def apply(ident: UUID, out: ActorRef): FrontendManager.Create = func(ident, out)
  }

  def apply(func: (UUID, ActorRef) => FrontendManager.Create): BackendRegisterFactory = {
    new BackendRegisterFactoryImpl(func)
  }
}
