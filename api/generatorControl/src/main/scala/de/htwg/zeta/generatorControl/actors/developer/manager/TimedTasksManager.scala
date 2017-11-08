package de.htwg.zeta.generatorControl.actors.developer.manager

import java.util.UUID
import java.util.concurrent.TimeUnit

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Cancellable
import akka.actor.Props
import com.google.inject.Injector
import de.htwg.zeta.common.models.document.Change
import de.htwg.zeta.common.models.document.Changed
import de.htwg.zeta.common.models.document.Created
import de.htwg.zeta.common.models.document.Deleted
import de.htwg.zeta.common.models.document.Updated
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.entity.TimedTask
import de.htwg.zeta.common.models.worker.RunTimedTask
import de.htwg.zeta.persistence.general.EntityRepository

private case class ExecuteTask(task: TimedTask)

object TimedTasksManager {
  def props(worker: ActorRef, injector: Injector): Props = Props(new TimedTasksManager(worker, injector))
}

class TimedTasksManager(worker: ActorRef, injector: Injector) extends Actor with ActorLogging {

  private val generatorPersistence = injector.getInstance(classOf[EntityRepository[Generator]])
  private val filterPersistence = injector.getInstance(classOf[EntityRepository[Filter]])
  private val generatorImagePersistence = injector.getInstance(classOf[EntityRepository[GeneratorImage]])
  private val timedTaskPersistence = injector.getInstance(classOf[EntityRepository[TimedTask]])

  private var schedules: Map[UUID, Cancellable] = Map()

  def create(task: TimedTask) = {
    val taskDelay = Duration(task.delay, TimeUnit.MINUTES)
    val taskInterval = Duration(task.interval, TimeUnit.MINUTES)
    val schedule = context.system.scheduler.schedule(taskDelay, taskInterval, self, ExecuteTask(task))
    schedules += (task.id -> schedule)
  }

  def update(task: TimedTask) = {
    schedules.get(task.id) match {
      case Some(x) => x.cancel()
      case None => // task did not exist
    }
    create(task)
  }

  def remove(task: TimedTask) = {
    schedules.get(task.id) match {
      case Some(x) => x.cancel()
      case None => // task did not exist
    }
    schedules -= task.id
  }

  def executeTask(task: TimedTask) = {
    val result = for {
      task <- timedTaskPersistence.read(task.id)
      filter <- filterPersistence.read(task.filterId)
      generator <- generatorPersistence.read(task.generatorId)
      image <- generatorImagePersistence.read(generator.imageId)
    } yield RunTimedTask(task.id, generator.id, filter.id, image.dockerImage)

    result.map {
      job => worker ! job
    }.recover {
      case e: Exception => log.error(e.toString)
    }
  }

  def receive = {
    case Changed(task: TimedTask, change: Change) => change match {
      case Created => create(task)
      case Updated => update(task)
      case Deleted => remove(task)
    }
    case ExecuteTask(task) => executeTask(task)
    case _ =>
  }
}
