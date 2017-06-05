package actors.developer.manager

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import de.htwg.zeta.persistence.general.Repository
import de.htwg.zeta.persistence.general.Persistence
import models.document.Changed
import models.document.Created
import models.document.Deleted
import models.document.ModelEntity
import models.document.Updated
import models.document.Filter
import models.worker.RerunFilterJob

object FiltersManager {
  def props(worker: ActorRef, repository: Repository) = Props(new FiltersManager(worker, repository))
}

class FiltersManager(worker: ActorRef, repository: Repository) extends Actor with ActorLogging {

  private val filterRepo: Persistence[Filter] = repository.filters

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
