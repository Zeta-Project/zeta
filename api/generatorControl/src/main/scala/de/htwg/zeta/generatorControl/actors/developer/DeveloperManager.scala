package de.htwg.zeta.generatorControl.actors.developer

import de.htwg.zeta.generatorControl.actors.common.ChangeFeed
import de.htwg.zeta.generatorControl.actors.common.Channel
import de.htwg.zeta.generatorControl.actors.common.Configuration
import de.htwg.zeta.generatorControl.actors.common.Developers

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.sharding.ClusterSharding

import de.htwg.zeta.common.models.document.Change
import de.htwg.zeta.common.models.document.Changed
import de.htwg.zeta.common.models.document.Created
import de.htwg.zeta.common.models.document.Deleted
import de.htwg.zeta.common.models.document.Settings
import de.htwg.zeta.common.models.document.Updated
import de.htwg.zeta.common.models.frontend.Init
import de.htwg.zeta.common.models.frontend.MessageEnvelope

object DeveloperManager {
  def props() = Props(new DeveloperManager())
}

class DeveloperManager() extends Actor with ActorLogging {
  val listeners: List[ActorRef] = List(self)
  val shard = ClusterSharding(context.system).shardRegion(Mediator.shardRegionName)

  /**
   * Initialise the mediator (root-actor) for the developer
   *
   * @param developer The developer for which to start the actor
   */
  def initDeveloper(developer: Settings): Unit = shard ! MessageEnvelope(developer.owner, Init(developer))

  def receive = {
    case Changed(developer: Settings, change: Change) => change match {
      case Created => initDeveloper(developer)
      case Updated => initDeveloper(developer)
      case Deleted => log.warning("Removal not yet implemented.")
    }
  }
}
