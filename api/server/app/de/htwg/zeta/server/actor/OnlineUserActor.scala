package de.htwg.zeta.server.actor

import scala.collection.mutable

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import de.htwg.zeta.server.actor.OnlineUserActor.AreaState
import de.htwg.zeta.server.actor.OnlineUserActor.ClientInfo
import de.htwg.zeta.server.actor.OnlineUserActor.ClientOffline
import de.htwg.zeta.server.actor.OnlineUserActor.ClientOnline
import de.htwg.zeta.server.silhouette.ZetaIdentity
import grizzled.slf4j.Logging
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnlineUserActorFactory @Inject()(implicit actorSystem: ActorSystem) {
  val onlineActor: ActorRef = actorSystem.actorOf(OnlineUserActor.props())
}

class OnlineUserActor() extends Actor with Logging {
  private case class AreaAccess(
      instance: ActorRef,
      user: ZetaIdentity,
      areaName: String
  )

  private val onlineUsers: mutable.Set[AreaAccess] = mutable.Set()

  override def receive: Receive = {
    case ClientOnline(id, area) =>
      logger.info(s"Online: ${id.fullName} in $area")
      onlineUsers.add(AreaAccess(sender(), id, area))
      onlineClientsInArea(area).foreach(u => u ! onlineClientsInArea(id, area))
    case ClientOffline(id, area) =>
      logger.info(s"Offline: ${id.fullName} in $area")
      onlineUsers.remove(AreaAccess(sender(), id, area))
      onlineClientsInArea(area).foreach(u => u ! onlineClientsInArea(id, area))
  }

  private def onlineClientsInArea(self: ZetaIdentity, area: String) =
    AreaState(
      onlineUsers.filter(_.areaName == area)
        .filter(_.user.email != self.email)
        .map(a => ClientInfo(a.user.fullName))
        .toList.distinct
    )

  private def onlineClientsInArea(area: String): List[ActorRef] =
    onlineUsers.filter(_.areaName == area)
      .map(_.instance).toList.distinct
}
object OnlineUserActor {
  case class ClientOnline(identity: ZetaIdentity, area: String)
  case class ClientOffline(identity: ZetaIdentity, area: String)

  case class ClientInfo(fullName: String)
  case class AreaState(onlineClients: List[ClientInfo])

  def props(): Props = Props(new OnlineUserActor())
}