package de.htwg.zeta.server.controller.generatorControlForwader

import java.net.URLEncoder

import akka.actor.ActorPath
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.client.ClusterClient
import akka.cluster.client.ClusterClientSettings
import akka.util.ByteString
import de.htwg.zeta.common.cluster.ClusterManager
import de.htwg.zeta.generatorControl.start.DeveloperStarter
import grizzled.slf4j.Logging

/**
 * @param clusterAddress in format s"${ip}:${port}"
 */
class BackendForwarder(clusterAddress: String) extends Actor with Logging {

  val encName: String = URLEncoder.encode(DeveloperStarter.ActorName, ByteString.UTF_8)
  private val receptionistPath: String = s"${ClusterManager.clusterPathPrefix}$clusterAddress/system/receptionist"
  private val initialContacts: Set[ActorPath] = Set(ActorPath.fromString(receptionistPath))
  private val settings: ClusterClientSettings = ClusterClientSettings(context.system).withInitialContacts(initialContacts)
  private val client: ActorRef = context.system.actorOf(ClusterClient.props(settings), "client")

  private def send(msg: Any): Unit = {
    // setting localAffinity to true is unnecessary as there is only one possible address.
    client ! ClusterClient.Send(s"/user/$encName", msg, localAffinity = false)
  }


  override def receive: Receive = {
    case _ => // TODO
  }
}

object BackendForwarder {
  def props(clusterAddress: String): Props = Props(new BackendForwarder(clusterAddress))
}
