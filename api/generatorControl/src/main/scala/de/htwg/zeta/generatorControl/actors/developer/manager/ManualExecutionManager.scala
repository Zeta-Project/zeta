package de.htwg.zeta.generatorControl.actors.developer.manager

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props

import de.htwg.zeta.common.models.document.GeneratorImage
import de.htwg.zeta.common.models.document.Repository
import de.htwg.zeta.common.models.document.Filter
import de.htwg.zeta.common.models.document.Generator
import de.htwg.zeta.common.models.frontend.ExecuteFilterError
import de.htwg.zeta.common.models.frontend.ExecuteGeneratorError
import de.htwg.zeta.common.models.frontend.RunFilter
import de.htwg.zeta.common.models.frontend.RunGenerator
import de.htwg.zeta.common.models.worker.RunFilterManually
import de.htwg.zeta.common.models.worker.RunGeneratorManually

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
