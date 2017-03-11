package actors.developer.manager

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import models.document._
import models.frontend.{ CreateGenerator, GeneratorImageNotFoundFailure }
import models.worker.{ CreateGeneratorJob }
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