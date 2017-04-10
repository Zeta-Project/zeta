package actors.developer.manager

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import models.document._
import models.frontend._
import models.worker._
import rx.lang.scala.Observable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise

object BondedTasksManager {
  def props(worker: ActorRef, repository: Repository) = Props(new BondedTasksManager(worker, repository))
  case class GetBondedTaskList(user: ModelUser)
}

class BondedTasksManager(worker: ActorRef, repository: Repository) extends Actor with ActorLogging {
  import BondedTasksManager._

  // 1. check if the bonded task exist
  // 2. get the filter attached to the bonded task
  // 3. check if the user (which triggered the task) can execute the task.
  def handleRequest(request: ExecuteBondedTask) = {
    val job = for {
      task <- repository.get[BondedTask](request.task)
      filter <- repository.get[Filter](task.filter)
      if filter.instances.contains(request.model)
      generator <- repository.get[Generator](task.generator)
      image <- repository.get[GeneratorImage](generator.image)
    } yield RunBondedTask(task.id, task.generator, filter.id, request.model, image.dockerImage)

    job.map {
      job => worker ! job
    }.recover {
      case e: Exception => {
        log.error(e.toString)
        sender ! BondedTaskNotExecutable(request.task, e.toString)
      }
    }
  }

  def entry(task: BondedTask, model: ModelUser): Future[Option[Entry]] = {
    val p = Promise[Option[Entry]]

    repository.get[Filter](task.filter).map { filter =>
      if (filter.instances.contains(model.model)) {
        p.success(Some(Entry(task.id, task.menu, task.item)))
      } else {
        p.success(None)
      }
    }.recover {
      case e: Exception => {
        log.error(e.getMessage)
        p.success(None)
      }
    }
    p.future
  }

  def getBondedTaskList(user: ModelUser): Unit = {
    repository.query[BondedTask](AllBondedTasks())
      .flatMap(task => Observable.from(entry(task, user)))
      .toList.subscribe(list => user.out ! BondedTaskList(list.flatten))
  }

  def receive = {
    // handle user request to execute a bonded task
    case request: ExecuteBondedTask => handleRequest(request)
    case request: GetBondedTaskList => getBondedTaskList(request.user)
  }
}
