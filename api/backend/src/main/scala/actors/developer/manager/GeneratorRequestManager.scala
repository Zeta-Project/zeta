package actors.developer.manager

import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import de.htwg.zeta.persistence.general.Repository
import models.frontend.RunGeneratorFromGenerator
import models.frontend.StartGeneratorError
import models.worker.RunGeneratorFromGeneratorJob

object GeneratorRequestManager {
  def props(workQueue: ActorRef, repository: Repository) = Props(new GeneratorRequestManager(workQueue, repository))
}

class GeneratorRequestManager(workQueue: ActorRef, repository: Repository) extends Actor with ActorLogging {
  // find the generator and filter and send a job to the worker
  def runGenerator(run: RunGeneratorFromGenerator) = {
    val reply = sender

    val result = for {
      generator <- repository.generators.read(run.generatorId)
      image <- repository.generatorImages.read(generator.imageId)
    } yield RunGeneratorFromGeneratorJob(
      parentId = run.parent,
      key = run.key,
      generatorId = run.generatorId,
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
