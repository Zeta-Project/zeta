package actors.worker

import actors.worker.MasterWorkerProtocol.{ CancelWork, Work }
import akka.actor.{ Actor, ActorLogging, Props }

object DummyWorkerExecutor {
  def props() = Props(new DummyWorkerExecutor())
}

class DummyWorkerExecutor() extends Actor with ActorLogging {
  def receive = {
    case work: Work =>
      sender ! Worker.WorkComplete(0)
    case cancel: CancelWork =>
      sender ! Worker.WorkComplete(1)
  }
}
