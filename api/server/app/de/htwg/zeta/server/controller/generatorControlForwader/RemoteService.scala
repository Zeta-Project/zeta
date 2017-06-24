package de.htwg.zeta.server.controller.generatorControlForwader

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.cluster.client.ClusterClient
import de.htwg.zeta.common.cluster.RemoteClient

/**
 */
class RemoteService(system: ActorSystem, serviceName: String, client: RemoteClient) {
  private val servicePath = s"/user/$serviceName"

  def send(msg: Any, sender: ActorRef): Unit = {
    // setting localAffinity to true is unnecessary.
    client.client.tell(ClusterClient.Send(servicePath, msg, localAffinity = false), sender)
  }
}

object RemoteService {
  def apply(system: ActorSystem, serviceName: String, settings: RemoteClient) = new RemoteService(system, serviceName, settings)
}
