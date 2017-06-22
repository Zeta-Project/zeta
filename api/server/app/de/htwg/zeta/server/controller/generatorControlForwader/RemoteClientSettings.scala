package de.htwg.zeta.server.controller.generatorControlForwader

import javax.inject.Inject

import akka.actor.ActorPath
import akka.actor.ActorSystem
import akka.cluster.client.ClusterClientSettings
import de.htwg.zeta.common.cluster.ClusterManager


class RemoteClientSettings @Inject()(system: ActorSystem, clusterAddressSettings: ClusterAddressSettings) {

  private val receptionistPath: String = s"${ClusterManager.clusterPathPrefix}${clusterAddressSettings.clusterAddress}/system/receptionist"
  private val initialContacts: Set[ActorPath] = Set(ActorPath.fromString(receptionistPath))
  val settings: ClusterClientSettings = ClusterClientSettings(system).withInitialContacts(initialContacts)

}

case class ClusterAddressSettings(ip: String, port: Int) {
  lazy val clusterAddress = s"${ip}:${port}"
}
