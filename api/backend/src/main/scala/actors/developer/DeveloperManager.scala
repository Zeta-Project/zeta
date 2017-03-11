package actors.developer

import actors.common.{ ChangeFeed, Channel, Configuration, Developers }
import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.cluster.sharding.ClusterSharding
import models.document._
import models.frontend.{ Init, MessageEnvelope }

object DeveloperManager {
  def props() = Props(new DeveloperManager())
}

class DeveloperManager() extends Actor with ActorLogging {
  val conf = Configuration()
  val channels: List[Channel] = List(Developers())
  val listeners: List[ActorRef] = List(self)
  val changeFeed = context.actorOf(ChangeFeed.props(conf, channels, listeners), name = "changeFeed")
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