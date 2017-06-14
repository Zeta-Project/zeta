package de.htwg.zeta.generatorControl.actors.image

import java.util.concurrent.Executors

import de.htwg.zeta.generatorControl.actors.common.ChangeFeed
import de.htwg.zeta.generatorControl.actors.common.Channel
import de.htwg.zeta.generatorControl.actors.common.Configuration
import de.htwg.zeta.generatorControl.actors.common.Images
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import de.htwg.zeta.common.models.document.Change

import scala.concurrent.ExecutionContext

object ImageManager {
  def props() = Props(new ImageManager())
}

class ImageManager() extends Actor with ActorLogging {
  private implicit val executionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
  val conf = Configuration()
  val channels: List[Channel] = List(Images())
  val listeners: List[ActorRef] = List(self)
  val changeFeed = context.actorOf(ChangeFeed.props(conf, channels, listeners), name = "changeFeed")

  def receive = {
    case change: Change => change match {
      case _ => log.info("")
    }
  }
}
