package actors.common

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Address
import akka.actor.Props
import akka.cluster.Cluster
import akka.cluster.ClusterEvent

object ClusterListener {
  def props(): Props = Props(new ClusterListener())
}

class ClusterListener extends Actor with ActorLogging {
  import ClusterEvent._

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
