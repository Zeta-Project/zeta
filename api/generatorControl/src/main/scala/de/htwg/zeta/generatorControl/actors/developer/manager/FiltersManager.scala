package de.htwg.zeta.generatorControl.actors.developer.manager

import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props

import de.htwg.zeta.common.models.entity.AllFilters
import de.htwg.zeta.common.models.entity.Change
import de.htwg.zeta.common.models.entity.Changed
import de.htwg.zeta.common.models.entity.Created
import de.htwg.zeta.common.models.entity.Deleted
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.entity.Repository
import de.htwg.zeta.common.models.entity.Updated
import de.htwg.zeta.common.models.worker.RerunFilterJob
import de.htwg.zeta.persistence.general.EntityPersistence
import de.htwg.zeta.persistence.general.Repository
import models.document.Changed
import models.document.Created
import models.document.Deleted
import models.document.Updated
import models.entity.Filter
import models.entity.ModelEntity
import models.worker.RerunFilterJob

object FiltersManager {
  def props(worker: ActorRef, repository: Repository) = Props(new FiltersManager(worker, repository))
}

class FiltersManager(worker: ActorRef, repository: Repository) extends Actor with ActorLogging {

  private val filterRepo: EntityPersistence[Filter] = repository.filter

  private def rerunFilter = {
    filterRepo.readAllIds().map(ids =>
      ids.foreach(id =>
        filterRepo.read(id).map(filter =>
          worker ! RerunFilterJob(filter.id)
        ).recover {
          case e: Exception => log.error(e.toString)
        }
      )
    )
  }

  def receive: Receive = {
    case Changed(model: ModelEntity, Created) => rerunFilter
    case Changed(model: ModelEntity, Updated) => // filter don't need to be rerun on model update
    case Changed(model: ModelEntity, Deleted) => rerunFilter
  }

}
