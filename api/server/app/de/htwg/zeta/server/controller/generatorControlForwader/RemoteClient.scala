package de.htwg.zeta.server.controller.generatorControlForwader

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.cluster.client.ClusterClient

/**
 */
class RemoteClient(system: ActorSystem, serviceName: String, settings: RemoteClientSettings) {
  private val client: ActorRef = system.actorOf(ClusterClient.props(settings.settings), s"${serviceName}Client")
  private val clientPath = s"/user/$serviceName"


  def send(msg: Any, sender: ActorRef): Unit = {
    // setting localAffinity to true is unnecessary.
    client.tell(ClusterClient.Send(clientPath, msg, localAffinity = false), sender)
  }
}

object RemoteClient {
  def apply(system: ActorSystem, serviceName: String, settings: RemoteClientSettings) = new RemoteClient(system, serviceName, settings)
}
