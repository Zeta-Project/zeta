package actors.developer.manager

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import de.htwg.zeta.persistence.general.Persistence
import models.document.GeneratorImage
import models.frontend.CreateGenerator
import models.frontend.GeneratorImageNotFoundFailure
import models.worker.CreateGeneratorJob

object GeneratorManager {
  def props(worker: ActorRef, repository: Persistence[GeneratorImage]) = Props(new GeneratorManager(worker, repository))
}

class GeneratorManager(worker: ActorRef, repository: Persistence[GeneratorImage]) extends Actor with ActorLogging {
  def createGenerator(create: CreateGenerator) = {
    val reply = sender
    repository.read(create.imageId).map(image =>
      worker ! CreateGeneratorJob(image.dockerImage, create.imageId, create.options)
    ).recover {
      case e: Exception => reply ! GeneratorImageNotFoundFailure(e.getMessage)
    }
  }

  def receive = {
    case create: CreateGenerator => createGenerator(create)
  }
}
