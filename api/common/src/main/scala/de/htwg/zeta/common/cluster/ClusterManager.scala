package de.htwg.zeta.common.cluster

import akka.actor.ActorPath
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.AddressFromURIString
import akka.actor.RootActorPath
import akka.cluster.client.ClusterClient
import akka.cluster.client.ClusterClientSettings
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

/**
 * Created by user on 9/28/16.
 */
object ClusterManager {
  def getClusterClient(system: ActorSystem, seeds: List[String]): ActorRef = {
    val parsedSeeds = seeds.map { address => s"""akka.tcp://ClusterSystem@${HostIP.lookupNodeAddress(address)}""" }

    val initialContacts = parsedSeeds.map {
      case AddressFromURIString(address) => RootActorPath(address) / "system" / "receptionist"
    }.toSet

    system.actorOf(
      ClusterClient.props(
        ClusterClientSettings(system)
          .withInitialContacts(initialContacts)
      ),
      "clusterClient"
    )
  }

  def getJournalPath(port: Int, seeds: List[String]): ActorPath = {
    if (seeds.isEmpty) {
      ActorPath.fromString(s"akka.tcp://ClusterSystem@${HostIP.load()}:${port}/user/store")
    } else {
      ActorPath.fromString(s"akka.tcp://ClusterSystem@${HostIP.lookupNodeAddress(seeds.head)}/user/store")
    }
  }

  def getClusterJoinConfig(roles: List[String], seeds: List[String], port: Int = 0): Config = {
    val formattedSeeds = (s"${HostIP.load()}:${port}" :: seeds).map {
      address => s"""akka.cluster.seed-nodes += "akka.tcp://ClusterSystem@${HostIP.lookupNodeAddress(address)}\""""
    }.mkString("\n")

    val formattedRoles = roles.mkString(",")

    val content = s"""akka.cluster.roles = [ ${formattedRoles} ]
      |akka.remote.netty.tcp.port=${port}
      |akka.remote.netty.tcp.hostname=${HostIP.load()}
      |${formattedSeeds}""".stripMargin

    ConfigFactory.parseString(content).resolve()
  }

  def getLocalConfig(port: Int): Config = {
    val content = s"""akka.remote.netty.tcp.port=${port}
      |akka.remote.netty.tcp.hostname=${HostIP.load()}""".stripMargin

    ConfigFactory.parseString(content).withFallback(ConfigFactory.load("worker"))
  }
}
