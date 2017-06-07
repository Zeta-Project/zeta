package actors.developer

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.sharding.ClusterSharding

import models.document.Change
import models.document.Changed
import models.document.Created
import models.document.Deleted
import models.document.Settings
import models.document.Updated
import models.frontend.Init
import models.frontend.MessageEnvelope

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
