package actors.developer.manager

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props

import models.document.AllEventDrivenTasks
import models.document.EventDrivenTask
import models.document.Filter
import models.document.Generator
import models.document.GeneratorImage
import models.document.Repository
import models.frontend.ModelChanged
import models.frontend.SavedModel
import models.worker.RunEventDrivenTask

import rx.lang.scala.Observable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise

object EventDrivenTasksManager {
  def props(worker: ActorRef, repository: Repository) = Props(new EventDrivenTasksManager(worker, repository))
}

class EventDrivenTasksManager(worker: ActorRef, repository: Repository) extends Actor with ActorLogging {

  def isListening(task: EventDrivenTask, changed: ModelChanged): Boolean = changed match {
    case SavedModel(model) => task.event == "Model update"
  }

  def check(task: EventDrivenTask, saved: SavedModel): Future[Option[RunEventDrivenTask]] = {
    val p = Promise[Option[RunEventDrivenTask]]

    val op = for {
      filter <- repository.get[Filter](task.filter)
      if filter.instances.contains(saved.model)
      generator <- repository.get[Generator](task.generator)
      image <- repository.get[GeneratorImage](generator.image)
    } yield Some(RunEventDrivenTask(task.id, generator.id, filter.id, saved.model, image.dockerImage))

    op.map { task =>
      p.success(task)
    }.recover {
      case e: Exception => p.success(None)
    }

    p.future
  }

  def onModelChange(changed: SavedModel): Unit = {
    repository.query[EventDrivenTask](AllEventDrivenTasks()).filter(isListening(_, changed))
      .flatMap(task => Observable.from(check(task, changed)))
      .filter(_.isDefined)
      .subscribe(task => worker ! task.get)
  }

  def receive = {
    case changed @ SavedModel(_) => onModelChange(changed)
    case _ =>
  }
}
