package de.htwg.zeta.generatorControl.actors.developer.manager

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.frontend.CreateGenerator
import de.htwg.zeta.common.models.frontend.GeneratorImageNotFoundFailure
import de.htwg.zeta.common.models.worker.CreateGeneratorJob
import de.htwg.zeta.persistence.general.EntityPersistence
import de.htwg.zeta.persistence.general.Repository

object GeneratorManager {
  def props(worker: ActorRef, repository: Repository): Props = Props(new GeneratorManager(worker, repository))
}

class GeneratorManager(worker: ActorRef, repository: Repository) extends Actor with ActorLogging {

  private val generatorImageRepo: EntityPersistence[GeneratorImage] = repository.generatorImage

  def createGenerator(create: CreateGenerator): Future[Unit] = {
    val reply = sender
    generatorImageRepo.read(create.imageId).map(image =>
      worker ! CreateGeneratorJob(image.dockerImage, create.imageId, create.options)
    ).recover {
      case e: Exception => reply ! GeneratorImageNotFoundFailure(e.getMessage)
    }
  }

  def receive: Receive = {
    case create: CreateGenerator => createGenerator(create)
  }
}
