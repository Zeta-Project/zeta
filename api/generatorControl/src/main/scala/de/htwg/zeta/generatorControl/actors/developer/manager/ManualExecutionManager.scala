package de.htwg.zeta.generatorControl.actors.developer.manager

import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import com.google.inject.Injector
import de.htwg.zeta.common.models.entity.BondedTask
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.frontend.ExecuteFilterError
import de.htwg.zeta.common.models.frontend.ExecuteGeneratorError
import de.htwg.zeta.common.models.frontend.RunFilter
import de.htwg.zeta.common.models.frontend.RunGenerator
import de.htwg.zeta.common.models.worker.RunFilterManually
import de.htwg.zeta.common.models.worker.RunGeneratorManually
import de.htwg.zeta.persistence.general.EntityRepository

object ManualExecutionManager {
  def props(worker: ActorRef, injector: Injector): Props = Props(new ManualExecutionManager(worker, injector))
}

class ManualExecutionManager(worker: ActorRef, injector: Injector) extends Actor with ActorLogging {

  private val generatorPersistence = injector.getInstance(classOf[EntityRepository[Generator]])
  private val filterPersistence = injector.getInstance(classOf[EntityRepository[Filter]])
  private val generatorImagePersistence = injector.getInstance(classOf[EntityRepository[GeneratorImage]])

  // find the generator and filter and send a job to the worker
  def runGenerator(run: RunGenerator) = {
    val reply = sender

    val result = for {
      generator <- generatorPersistence.read(run.generatorId)
      filter <- filterPersistence.read(run.filterId)
      image <- generatorImagePersistence.read(generator.imageId)
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
      filter <- filterPersistence.read(run.filterId)
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
