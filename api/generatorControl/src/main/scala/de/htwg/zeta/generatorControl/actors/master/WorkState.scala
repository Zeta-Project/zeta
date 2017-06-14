package de.htwg.zeta.generatorControl.actors.master

import de.htwg.zeta.generatorControl.actors.worker.MasterWorkerProtocol.Work
import scala.collection.immutable.Queue

// wrapper to hold completed work
case class CompletedWork(work: Work, result: Int)

sealed class WorkDomainEvent
case class WorkAccepted(work: Work) extends WorkDomainEvent
case class WorkStarted(workId: String) extends WorkDomainEvent
case class WorkCompleted(workId: String, result: Int) extends WorkDomainEvent
case class WorkCompletionReceived(workId: String) extends WorkDomainEvent
case class WorkerFailed(workId: String) extends WorkDomainEvent
case class WorkerTimedOut(workId: String) extends WorkDomainEvent

object WorkState {

  def empty(): WorkState = WorkState(
    pendingWork = Queue.empty,
    workInProgress = Map.empty,
    acceptedWorkIds = Set.empty,
    completedWork = Map.empty
  )
}

case class WorkState private (
    private val pendingWork: Queue[Work],
    private val workInProgress: Map[String, Work],
    private val acceptedWorkIds: Set[String],
    private val completedWork: Map[String, CompletedWork]
) {
  def hasWork: Boolean = pendingWork.nonEmpty
  def nextWork: Work = pendingWork.head
  def isAccepted(workId: String): Boolean = acceptedWorkIds.contains(workId)
  def isInProgress(workId: String): Boolean = workInProgress.contains(workId)
  def isDone(workId: String): Boolean = completedWork.contains(workId)
  def completedWorkList() = completedWork.values
  def completedWorkById(workId: String): Option[CompletedWork] = completedWork.get(workId)

  def updated(event: WorkDomainEvent): WorkState = event match {
    case WorkAccepted(work) =>
      copy(
        pendingWork = pendingWork enqueue work,
        acceptedWorkIds = acceptedWorkIds + work.id
      )

    case WorkStarted(workId) =>
      val (work, rest) = pendingWork.dequeue
      require(workId == work.id, s"WorkStarted expected workId $workId == ${work.id}")
      copy(
        pendingWork = rest,
        workInProgress = workInProgress + (workId -> work)
      )

    case WorkCompleted(workId, result) =>
      val work = workInProgress(workId)
      copy(
        workInProgress = workInProgress - workId,
        completedWork = completedWork + (workId -> CompletedWork(work, result))
      )

    case WorkCompletionReceived(workId) =>
      copy(
        completedWork = completedWork - workId
      )

    case WorkerFailed(workId) =>
      copy(
        pendingWork = pendingWork enqueue workInProgress(workId),
        workInProgress = workInProgress - workId
      )

    case WorkerTimedOut(workId) =>
      copy(
        pendingWork = pendingWork enqueue workInProgress(workId),
        workInProgress = workInProgress - workId
      )
  }
}
