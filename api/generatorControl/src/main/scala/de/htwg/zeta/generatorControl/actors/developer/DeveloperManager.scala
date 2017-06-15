package de.htwg.zeta.generatorControl.actors.developer

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.sharding.ClusterSharding
import de.htwg.zeta.common.models.document.Change
import de.htwg.zeta.common.models.document.Changed
import de.htwg.zeta.common.models.document.Created
import de.htwg.zeta.common.models.document.Deleted
import de.htwg.zeta.common.models.document.Updated
import de.htwg.zeta.common.models.entity.Settings
import de.htwg.zeta.common.models.frontend.Init
import de.htwg.zeta.common.models.frontend.MessageEnvelope

object DeveloperManager {
  def props(): Props = Props(new DeveloperManager())
}

class DeveloperManager() extends Actor with ActorLogging {
  val listeners: List[ActorRef] = List(self)
  val shard: ActorRef = ClusterSharding(context.system).shardRegion(Mediator.shardRegionName)

  /**
   * Initialise the mediator (root-actor) for the developer
   *
   * @param developer The developer for which to start the actor
   */
  def initDeveloper(developer: Settings): Unit = shard ! MessageEnvelope(developer.owner, Init(developer))

  def receive: Receive = {
    case Changed(developer: Settings, change: Change) => change match {
      case Created => initDeveloper(developer)
      case Updated => initDeveloper(developer)
      case Deleted => log.warning("Removal not yet implemented.")
    }
  }
}
