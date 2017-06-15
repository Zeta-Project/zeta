package de.htwg.zeta.generatorControl.actors.common

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Address
import akka.actor.Props
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.InitialStateAsEvents
import akka.cluster.ClusterEvent.MemberEvent
import akka.cluster.ClusterEvent.MemberJoined
import akka.cluster.ClusterEvent.MemberRemoved
import akka.cluster.ClusterEvent.MemberUp

object ClusterListener {
  def props(): Props = Props(new ClusterListener())
}

class ClusterListener extends Actor with ActorLogging {
  private var members = Set.empty[Address]

  Cluster(context.system).subscribe(self, InitialStateAsEvents, classOf[MemberEvent])

  log.info("Started ClusterListener")

  override def receive = {
    case MemberJoined(member) =>
      log.info("Member joined: {}", member.address)
      members += member.address

    case MemberUp(member) =>
      log.info("Member up: {}", member.address)
      members += member.address

    case MemberRemoved(member, _) =>
      log.info("Member removed: {}", member.address)
      members -= member.address
  }
}
