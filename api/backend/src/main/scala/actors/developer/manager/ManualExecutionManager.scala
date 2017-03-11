package actors.developer.manager

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import models.document._
import models.document.{ Filter, Generator }
import models.frontend.{ ExecuteFilterError, ExecuteGeneratorError, RunFilter, RunGenerator }
import models.worker.{ RunFilterManually, RunGeneratorManually }
import scala.concurrent.ExecutionContext.Implicits.global

object ManualExecutionManager {
  def props(worker: ActorRef, repository: Repository) = Props(new ManualExecutionManager(worker, repository))
}

class ManualExecutionManager(worker: ActorRef, repository: Repository) extends Actor with ActorLogging {
  // find the generator and filter and send a job to the worker
  def runGenerator(run: RunGenerator) = {
    val reply = sender

    val result = for {
      generator <- repository.get[Generator](run.generator)
      filter <- repository.get[Filter](run.filter)
      image <- repository.get[GeneratorImage](generator.image)
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
      filter <- repository.get[Filter](run.filter)
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
