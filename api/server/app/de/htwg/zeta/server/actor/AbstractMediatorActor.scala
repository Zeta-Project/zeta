package de.htwg.zeta.server.actor

import scala.collection.mutable

import akka.actor.ActorRef
import akka.actor.Actor
import akka.actor.Terminated

/**
 */
abstract class AbstractMediatorActor extends Actor {
  private val subscriberCache: mutable.HashMap[String, mutable.HashSet[ActorRef]] = mutable.HashMap.empty

  override final def receive: Receive = mediatorReceive.orElse(childReceive)

  protected def childReceive: Receive

  private val mediatorReceive: Receive = {
    case AbstractMediatorActor.SubscribeTo(topic) => handleSubscribeTo(topic)
    case Terminated(deadRef) => handleSubscriberDeath(deadRef)
  }

  protected def publish(topic: String, msg: Any): Unit = {
    subscriberCache.get(topic).foreach(_.foreach(_.tell(msg, self)))
  }

  private def handleSubscribeTo(topic: String): Unit = {
    val set = subscriberCache.getOrElseUpdate(topic, mutable.HashSet.empty)
    val sub = sender()
    context.watch(sub)
    set.add(sender())
  }

  private def handleSubscriberDeath(deadRef: ActorRef): Unit = {
    var toDelete: List[String] = Nil
    subscriberCache.foreach(pair => {
      val key = pair._1
      val set = pair._2

      if (set.remove(deadRef) && set.isEmpty) {
        // safe to var. Do not alter map while iterating over it.
        toDelete = key :: toDelete
      }
    })

    toDelete.foreach(subscriberCache.remove)
  }
}

object AbstractMediatorActor {

  case class SubscribeTo(topic: String)

}
