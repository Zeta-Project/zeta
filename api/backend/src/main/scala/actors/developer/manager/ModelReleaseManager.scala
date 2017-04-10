package actors.developer.manager

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import models._
import models.frontend.RunModelRelease
import models.worker.CreateMetaModelReleaseJob

object ModelReleaseManager {
  def props(worker: ActorRef) = Props(new ModelReleaseManager(worker))
}

class ModelReleaseManager(worker: ActorRef) extends Actor with ActorLogging {
  def receive = {
    case RunModelRelease(model) => worker ! CreateMetaModelReleaseJob(model)
  }
}
