package actors.developer.manager

import java.util.UUID

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import de.htwg.zeta.persistence.general.Repository
import models.document.Filter
import models.frontend.BondedTaskList
import models.frontend.BondedTaskNotExecutable
import models.frontend.Entry
import models.frontend.ExecuteBondedTask
import models.frontend.ModelUser
import models.worker.RunBondedTask

case class GetBondedTaskList(user: ModelUser)

object BondedTasksManager {
  def props(worker: ActorRef, repository: Repository) = Props(new BondedTasksManager(worker, repository))
}

class BondedTasksManager(worker: ActorRef, repository: Repository) extends Actor with ActorLogging {
  // 1. check if the bonded task exist
  // 2. get the filter attached to the bonded task
  // 3. check if the user (which triggered the task) can execute the task.
  def handleRequest(request: ExecuteBondedTask) = {
    val job = for {
      task <- repository.bondTask.read(request.taskId) // repository.get[BondedTask](request.task)
      filter <- repository.filter.read(task.filterId)
      if filter.instanceIds.contains(request.modelId)
      generator <- repository.generator.read(task.generatorId)
      image <- repository.generatorImage.read(generator.imageId)
    } yield {
      RunBondedTask(task.id, task.generatorId, filter.id, request.modelId, image.dockerImage)
    }

    job.map {
      job => worker ! job
    }.recover {
      case e: Exception => {
        log.error(e.toString)
        sender ! BondedTaskNotExecutable(request.taskId, e.toString)
      }
    }
  }


  def getBondedTaskList(user: ModelUser): Unit = {

    repository.bondTask.readAllIds().flatMap(ids => {
      Future.sequence(ids.map(repository.bondTask.read)).map(bondedTasks => {
        val filters = bondedTasks.map(bondTask =>
          (bondTask.id, repository.filter.read(bondTask.filterId))
        ).map {
          case (id, filter) => filter.map(filter => (id, filter))
        }.toMap[UUID, Filter]
        val taskList = bondedTasks.filter(bondTask =>
          filters(bondTask.id).instanceIds.contains(user.modelId)).map(task =>
          Entry(task.id, task.menu, task.item)).toList
        user.out ! BondedTaskList(taskList)
      })
    })
  }

  def receive = {
    // handle user request to execute a bonded task
    case request: ExecuteBondedTask => handleRequest(request)
    case request: GetBondedTaskList => getBondedTaskList(request.user)
  }

}
