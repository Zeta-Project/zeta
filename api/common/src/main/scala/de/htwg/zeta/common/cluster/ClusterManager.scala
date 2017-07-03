package de.htwg.zeta.common.cluster

import akka.actor.ActorPath
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

/**
 */
object ClusterManager {
  val clusterPathPrefix = "akka.tcp://ClusterSystem@"

  def getJournalPath(port: Int, seeds: List[String]): ActorPath = {
    seeds.headOption match {
      case None =>
        ActorPath.fromString(s"$clusterPathPrefix${HostIP.load()}:${port}/user/store")

      case Some(seed) =>
        ActorPath.fromString(s"$clusterPathPrefix${HostIP.lookupNodeAddress(seed)}/user/store")
    }
  }

  def getClusterJoinConfig(roles: List[String], seeds: List[String], port: Int = 0): Config = {
    val formattedSeeds = (s"${HostIP.load()}:${port}" :: seeds).map {
      address => s"""akka.cluster.seed-nodes += "$clusterPathPrefix${HostIP.lookupNodeAddress(address)}\""""
    }.mkString("\n")

    val formattedRoles = roles.mkString(",")

    val content =
      s"""akka.cluster.roles = [ ${formattedRoles} ]
        |${formattedSeeds}""".stripMargin

    ConfigFactory.parseString(content).withFallback(getLocalNettyConfig(port))
  }

  def getLocalNettyConfig(port: Int): Config = {
    val content =
      s"""akka.remote.netty.tcp.port=${port}
        |akka.remote.netty.tcp.hostname=${HostIP.load()}""".stripMargin

    ConfigFactory.parseString(content)
  }
}
