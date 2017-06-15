package de.htwg.zeta.generatorControl.actors.developer.manager

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import de.htwg.zeta.common.models.entity.EventDrivenTask
import de.htwg.zeta.common.models.frontend.ModelChanged
import de.htwg.zeta.common.models.frontend.SavedModel
import de.htwg.zeta.common.models.worker.RunEventDrivenTask
import de.htwg.zeta.persistence.general.Repository

object EventDrivenTasksManager {
  def props(worker: ActorRef, repository: Repository) = Props(new EventDrivenTasksManager(worker, repository))
}

class EventDrivenTasksManager(worker: ActorRef, repository: Repository) extends Actor with ActorLogging {

  def isListening(task: EventDrivenTask, changed: ModelChanged): Boolean = {
    changed match {
      case SavedModel(model) => task.event == "Model update"
    }
  }

  def check(task: EventDrivenTask, saved: SavedModel): Future[Option[RunEventDrivenTask]] = {
    val p = Promise[Option[RunEventDrivenTask]]

    val op = for {
      filter <- repository.filter.read(task.filterId)
      if filter.instanceIds.contains(saved.modelId)
      generator <- repository.generator.read(task.generatorId)
      image <- repository.generatorImage.read(generator.imageId)
    } yield {
      Some(RunEventDrivenTask(task.id, generator.id, filter.id, saved.modelId, image.dockerImage))
    }

    op.map { task =>
      p.success(task)
    }.recover {
      case e: Exception => p.success(None)
    }

    p.future
  }

  def onModelChange(changed: SavedModel): Unit = {
    val allTaskIds = repository.eventDrivenTask.readAllIds()
    val allTasks = allTaskIds.flatMap { ids => Future.sequence(ids.map(repository.eventDrivenTask.read)) }
    val listeningTasks = allTasks.map(_.filter(isListening(_, changed)))
    val filteredTasks = listeningTasks.flatMap(x => Future.sequence(x.map(i => check(i, changed))).map(_.flatten))
    filteredTasks.foreach(task => worker ! task)
  }

  def receive: Receive = {
    case changed@SavedModel(_) => onModelChange(changed)
    case _ =>
  }
}
