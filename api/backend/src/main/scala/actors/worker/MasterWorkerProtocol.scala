package actors.worker

import java.util.UUID

import models.document.DockerSettings
import models.frontend.JobLog
import models.worker.Job

object MasterWorkerProtocol {
  // Messages from Workers to Master
  trait WorkerToMaster
  case class RegisterWorker(workerId: String) extends WorkerToMaster
  case class WorkerRequestsWork(workerId: String) extends WorkerToMaster
  case class WorkIsDone(workerId: String, workId: String, result: Int) extends WorkerToMaster
  case class WorkFailed(workerId: String, workId: String) extends WorkerToMaster

  // Messages to Workers from Master
  trait MasterToWorker
  case object WorkIsReady extends MasterToWorker
  case class Ack(id: String) extends MasterToWorker

  // Messages to Master from Developer
  trait DeveloperToMaster
  case class Work(
      job: Job,
      owner: UUID,
      dockerSettings: DockerSettings,
      session: String = "",
      id: String = UUID.randomUUID().toString)
    extends DeveloperToMaster
  case class CancelWork(id: String) extends DeveloperToMaster
  case class DeveloperReceivedCompletedWork(id: String) extends DeveloperToMaster

  trait ToDeveloper

  // Messages from Master to Developer
  trait MasterToDeveloper extends ToDeveloper
  case class MasterAcceptedWork(workerId: String) extends MasterToDeveloper
  case class MasterCompletedWork(workerId: String, result: Int) extends MasterToDeveloper

  // Messages from Worker to Developer
  trait WorkerToDeveloper extends ToDeveloper
  case class WorkerStreamedMessage(jobLog: JobLog) extends WorkerToDeveloper
}
