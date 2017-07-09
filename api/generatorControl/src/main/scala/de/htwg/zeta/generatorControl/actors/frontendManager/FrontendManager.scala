package de.htwg.zeta.generatorControl.actors.frontendManager

import java.util.UUID

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorRef
import akka.cluster.sharding.ClusterSharding
import de.htwg.zeta.generatorControl.actors.developer.Mediator
import de.htwg.zeta.generatorControl.actors.frontendManager.FrontendManager.ForwardMessage
import de.htwg.zeta.generatorControl.actors.frontendManager.FrontendManager.KeepHandlerAlive
import de.htwg.zeta.generatorControl.actors.frontendManager.FrontendManager.KeepAlive
import de.htwg.zeta.generatorControl.actors.frontendManager.FrontendManager.TerminateFrontend
import de.htwg.zeta.generatorControl.actors.frontendManager.FrontendManager.Terminate
import de.htwg.zeta.generatorControl.actors.frontendManager.FrontendManager.Create
import grizzled.slf4j.Logging

/**
 */
class FrontendManager[R <: Create](gen: FrontendManagerGenerator) extends Actor with Logging {
  private val devMediator: ActorRef = ClusterSharding(context.system).shardRegion(Mediator.shardRegionName)



  private def tellChild(ident: UUID, msg: Any): Unit = {
    context.child(ident.toString) match {
      case Some (ref) => ref ! msg
      case None =>  warn(s"failed to send msg: $msg to: $ident. No child with this UUID was found.")
    }
  }

  override def receive: Receive = {
    case gen(uuid, props) =>
      val ident = uuid.toString
      context.child(ident) match {
        case None =>
          context.actorOf(props(devMediator), ident)
        case Some(_) =>
          info(s"actor with id: $ident already exists")
      }
    case ForwardMessage(ident, msg) => tellChild(ident, msg)
    case KeepHandlerAlive(ident) => tellChild(ident, KeepAlive)
    case TerminateFrontend(ident) => tellChild(ident, Terminate)
  }
}

object FrontendManager {


  trait FrontendManagerMessage {
    val ident: UUID
  }

  trait Create extends FrontendManagerMessage {
    val out: ActorRef
  }

  case class ForwardMessage(ident: UUID, msg: Any) extends FrontendManagerMessage

  case class KeepHandlerAlive(ident: UUID) extends FrontendManagerMessage

  case class TerminateFrontend(ident: UUID) extends FrontendManagerMessage

  case object KeepAlive

  case object Terminate

  def props(gen: FrontendManagerGenerator): Props = Props(new FrontendManager(gen))
}

trait FrontendManagerGenerator {

  /**
   *
   * @param create the [[Create]] message
   * @return a function that will take a reference to [[Mediator.shardRegionName]] and returns Props
   */
  def unapply(create: Create): Option[(UUID, (ActorRef) => Props)]
}
