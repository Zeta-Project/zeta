package de.htwg.zeta.generatorControl.actors.developer.manager

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props

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
