package de.htwg.zeta.generatorControl.actors.developer.manager

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import com.google.inject.Injector
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.frontend.RunGeneratorFromGenerator
import de.htwg.zeta.common.models.frontend.StartGeneratorError
import de.htwg.zeta.common.models.worker.RunGeneratorFromGeneratorJob
import de.htwg.zeta.persistence.general.EntityRepository

object GeneratorRequestManager {
  def props(workQueue: ActorRef, injector: Injector): Props = Props(new GeneratorRequestManager(workQueue, injector))
}

class GeneratorRequestManager(workQueue: ActorRef, injector: Injector) extends Actor with ActorLogging {

  private val generatorPersistence = injector.getInstance(classOf[EntityRepository[Generator]])
  private val generatorImagePersistence = injector.getInstance(classOf[EntityRepository[GeneratorImage]])

  // find the generator and filter and send a job to the worker
  def runGenerator(run: RunGeneratorFromGenerator): Future[Unit] = {
    val reply = sender

    val result = for {
      generator <- generatorPersistence.read(run.generatorId)
      image <- generatorImagePersistence.read(generator.imageId)
    } yield {
      RunGeneratorFromGeneratorJob(
        parentId = run.parent,
        key = run.key,
        generatorId = run.generatorId,
        image = image.dockerImage,
        options = run.options
      )
    }

    result.map { job =>
      workQueue ! job
    }.recover {
      case e: Exception =>
        log.info(e.getMessage)
        reply ! StartGeneratorError(key = run.key, reason = e.getMessage)
    }
  }

  def receive: Receive = {
    case run: RunGeneratorFromGenerator => runGenerator(run)
  }
}
