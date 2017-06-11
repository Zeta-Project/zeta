package actors.developer.manager

import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import de.htwg.zeta.persistence.general.Persistence
import de.htwg.zeta.persistence.general.Repository
import models.entity.GeneratorImage
import models.frontend.CreateGenerator
import models.frontend.GeneratorImageNotFoundFailure
import models.worker.CreateGeneratorJob

object GeneratorManager {
  def props(worker: ActorRef, repository: Repository) = Props(new GeneratorManager(worker, repository))
}

class GeneratorManager(worker: ActorRef, repository: Repository) extends Actor with ActorLogging {

  private val generatorImageRepo: Persistence[GeneratorImage] = repository.generatorImages

  def createGenerator(create: CreateGenerator) = {
    val reply = sender
    generatorImageRepo.read(create.imageId).map(image =>
      worker ! CreateGeneratorJob(image.dockerImage, create.imageId, create.options)
    ).recover {
      case e: Exception => reply ! GeneratorImageNotFoundFailure(e.getMessage)
    }
  }

  def receive = {
    case create: CreateGenerator => createGenerator(create)
  }
}
