package actors.developer.manager

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props

import models.document.Generator
import models.document.GeneratorImage
import models.document.Repository
import models.frontend.RunGeneratorFromGenerator
import models.frontend.StartGeneratorError
import models.worker.RunGeneratorFromGeneratorJob

import scala.concurrent.ExecutionContext.Implicits.global

object GeneratorRequestManager {
  def props(workQueue: ActorRef, repository: Repository) = Props(new GeneratorRequestManager(workQueue, repository))
}

class GeneratorRequestManager(workQueue: ActorRef, repository: Repository) extends Actor with ActorLogging {
  // find the generator and filter and send a job to the worker
  def runGenerator(run: RunGeneratorFromGenerator) = {
    val reply = sender

    val result = for {
      generator <- repository.get[Generator](run.generator)
      image <- repository.get[GeneratorImage](generator.imageId)
    } yield RunGeneratorFromGeneratorJob(
      parentId = run.parent,
      key = run.key,
      generator = run.generator,
      image = image.dockerImage,
      options = run.options
    )

    result.map { job =>
      workQueue ! job
    }.recover {
      case e: Exception => {
        log.info(e.getMessage)
        reply ! StartGeneratorError(key = run.key, reason = e.getMessage)
      }
    }
  }

  def receive = {
    case run: RunGeneratorFromGenerator => runGenerator(run)
  }
}
