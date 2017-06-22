package de.htwg.zeta.server.controller.generatorControlForwader

import javax.inject.Inject

import akka.actor.ActorPath
import akka.actor.ActorSystem
import akka.cluster.client.ClusterClientSettings
import de.htwg.zeta.common.cluster.ClusterManager


class RemoteClientSettings @Inject()(system: ActorSystem, clusterAddressSettings: ClusterAddressSettings) {

  private val initialContacts: Set[ActorPath] = clusterAddressSettings.addresses.map(clusterAddress => {
    s"${ClusterManager.clusterPathPrefix}${clusterAddress}/system/receptionist"
  }).map(ActorPath.fromString).toSet

  val settings: ClusterClientSettings = ClusterClientSettings(system).withInitialContacts(initialContacts)

}

/**
 * @param addresses list of addresses in format s"${ip}:${port}"
 */
case class ClusterAddressSettings(addresses: List[String])
