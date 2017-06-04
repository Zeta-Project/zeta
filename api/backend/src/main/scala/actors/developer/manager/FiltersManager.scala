package actors.developer.manager

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import de.htwg.zeta.persistence.general.Persistence
import models.document.Changed
import models.document.Created
import models.document.Deleted
import models.document.Filter
import models.document.ModelEntity
import models.document.Updated
import models.worker.RerunFilterJob

object FiltersManager {
  def props(worker: ActorRef, repository: Persistence[Filter]) = Props(new FiltersManager(worker, repository))
}

class FiltersManager(worker: ActorRef, repository: Persistence[Filter]) extends Actor with ActorLogging {

  private def rerunFilter = {
    repository.readAllIds().map(ids =>
      ids.foreach(id =>
        repository.read(id).map(filter =>
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
