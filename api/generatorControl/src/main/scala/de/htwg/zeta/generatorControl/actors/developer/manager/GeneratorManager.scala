package de.htwg.zeta.generatorControl.actors.developer.manager

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props

import de.htwg.zeta.common.models.document.GeneratorImage
import de.htwg.zeta.common.models.document.Repository
import de.htwg.zeta.common.models.frontend.CreateGenerator
import de.htwg.zeta.common.models.frontend.GeneratorImageNotFoundFailure
import de.htwg.zeta.common.models.worker.CreateGeneratorJob

import scala.concurrent.ExecutionContext.Implicits.global

object GeneratorsManager {
  def props(worker: ActorRef, repository: Repository) = Props(new GeneratorsManager(worker, repository))
}

class GeneratorsManager(worker: ActorRef, repository: Repository) extends Actor with ActorLogging {
  def createGenerator(create: CreateGenerator) = {
    val reply = sender
    repository.get[GeneratorImage](create.image)
      .map { image =>
        worker ! CreateGeneratorJob(image.dockerImage, create.image, create.options)
      }.recover {
        case e: Exception => reply ! GeneratorImageNotFoundFailure(e.getMessage)
      }
  }

  def receive = {
    case create: CreateGenerator => createGenerator(create)
    case _ =>
  }
}
