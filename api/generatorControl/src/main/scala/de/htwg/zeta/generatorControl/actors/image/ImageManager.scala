package de.htwg.zeta.generatorControl.actors.image

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import de.htwg.zeta.common.models.document.Change

object ImageManager {
  def props() = Props(new ImageManager())
}

class ImageManager() extends Actor with ActorLogging {
  private implicit val executionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))

  val listeners: List[ActorRef] = List(self)

  def receive = {
    case change: Change => change match {
      case _ => log.info("")
    }
  }
}
