package de.htwg.zeta.generatorControl.actors.worker

import de.htwg.zeta.generatorControl.actors.worker.MasterWorkerProtocol.CancelWork
import de.htwg.zeta.generatorControl.actors.worker.MasterWorkerProtocol.Work
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props

object DummyWorkerExecutor {
  def props() = Props(new DummyWorkerExecutor())
}

class DummyWorkerExecutor() extends Actor with ActorLogging {
  def receive = {
    case work: Work =>
      sender ! WorkComplete(0)
    case cancel: CancelWork =>
      sender ! WorkComplete(1)
  }
}
