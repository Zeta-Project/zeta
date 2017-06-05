package actors.developer.manager

import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import de.htwg.zeta.persistence.general.Repository
import models.frontend.ExecuteFilterError
import models.frontend.ExecuteGeneratorError
import models.frontend.RunFilter
import models.frontend.RunGenerator
import models.worker.RunFilterManually
import models.worker.RunGeneratorManually

object ManualExecutionManager {
  def props(worker: ActorRef, repository: Repository) = Props(new ManualExecutionManager(worker, repository))
}

class ManualExecutionManager(worker: ActorRef, repository: Repository) extends Actor with ActorLogging {
  // find the generator and filter and send a job to the worker
  def runGenerator(run: RunGenerator) = {
    val reply = sender

    val result = for {
      generator <- repository.generators.read(run.generatorId)
      filter <- repository.filters.read(run.filterId)
      image <- repository.generatorImages.read(generator.imageId)
    } yield RunGeneratorManually(generator.id, image.dockerImage, filter.id)

    result.map { job =>
      worker ! job
    }.recover {
      case e: Exception => reply ! ExecuteGeneratorError(e.getMessage)
    }
  }

  // find the filter and send a job to the worker
  def runFilter(run: RunFilter): Unit = {
    val reply = sender

    val result = for {
      filter <- repository.filters.read(run.filterId)
    } yield RunFilterManually(filter.id)

    result.map { job =>
      worker ! job
    }.recover {
      case e: Exception => reply ! ExecuteFilterError(e.getMessage)
    }
  }

  def receive = {
    case run: RunGenerator => runGenerator(run)
    case run: RunFilter => runFilter(run)
    case _ =>
  }
}
