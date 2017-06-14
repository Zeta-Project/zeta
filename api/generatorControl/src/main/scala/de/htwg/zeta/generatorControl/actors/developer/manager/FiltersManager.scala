package de.htwg.zeta.generatorControl.actors.developer.manager

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props

import models.document.AllFilters
import models.document.Change
import models.document.Changed
import models.document.Created
import models.document.Deleted
import models.document.Filter
import models.document.ModelEntity
import models.document.Repository
import models.document.Updated
import models.worker.RerunFilterJob

import rx.lang.scala.Notification.OnError
import rx.lang.scala.Notification.OnNext

object FiltersManager {
  def props(worker: ActorRef, repository: Repository) = Props(new FiltersManager(worker, repository))
}

class FiltersManager(worker: ActorRef, repository: Repository) extends Actor with ActorLogging {
  def rerunFilter = {
    repository.query[Filter](AllFilters()).materialize.subscribe(n => n match {
      case OnError(err) => log.error(err.toString)
      case OnNext(filter) => worker ! RerunFilterJob(filter.id())
    })
  }

  def receive = {
    case Changed(model: ModelEntity, change: Change) => change match {
      case Created => rerunFilter
      case Updated => // filter don't need to be rerun on model update
      case Deleted => rerunFilter
    }
    case _ =>
  }
}
