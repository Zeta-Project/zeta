package de.htwg.zeta.generatorControl.actors.developer.manager

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props

import de.htwg.zeta.common.models.document.AllFilters
import de.htwg.zeta.common.models.document.Change
import de.htwg.zeta.common.models.document.Changed
import de.htwg.zeta.common.models.document.Created
import de.htwg.zeta.common.models.document.Deleted
import de.htwg.zeta.common.models.document.Filter
import de.htwg.zeta.common.models.document.ModelEntity
import de.htwg.zeta.common.models.document.Repository
import de.htwg.zeta.common.models.document.Updated
import de.htwg.zeta.common.models.worker.RerunFilterJob

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
