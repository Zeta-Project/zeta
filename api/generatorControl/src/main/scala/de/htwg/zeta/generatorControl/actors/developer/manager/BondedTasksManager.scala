package de.htwg.zeta.generatorControl.actors.developer.manager

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import com.google.inject.Injector
import de.htwg.zeta.common.models.entity.BondedTask
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.frontend.BondedTaskList
import de.htwg.zeta.common.models.frontend.BondedTaskNotExecutable
import de.htwg.zeta.common.models.frontend.Entry
import de.htwg.zeta.common.models.frontend.ExecuteBondedTask
import de.htwg.zeta.common.models.frontend.ModelUser
import de.htwg.zeta.common.models.worker.RunBondedTask
import de.htwg.zeta.persistence.general.EntityRepository

case class GetBondedTaskList(user: ModelUser)

object BondedTasksManager {
  def props(worker: ActorRef, injector: Injector): Props = Props(new BondedTasksManager(worker, injector))
}

class BondedTasksManager(worker: ActorRef, injector: Injector) extends Actor with ActorLogging {

  private val generatorPersistence = injector.getInstance(classOf[EntityRepository[Generator]])
  private val filterPersistence = injector.getInstance(classOf[EntityRepository[Filter]])
  private val generatorImagePersistence = injector.getInstance(classOf[EntityRepository[GeneratorImage]])
  private val bondedTaskPersistence = injector.getInstance(classOf[EntityRepository[BondedTask]])

  // 1. check if the bonded task exist
  // 2. get the filter attached to the bonded task
  // 3. check if the user (which triggered the task) can execute the task.
  def handleRequest(request: ExecuteBondedTask): Future[Unit] = {
    val job = for {
      task <- bondedTaskPersistence.read(request.taskId) // repository.get[BondedTask](request.task)
      filter <- filterPersistence.read(task.filterId)
      if filter.instanceIds.contains(request.modelId)
      generator <- generatorPersistence.read(task.generatorId)
      image <- generatorImagePersistence.read(generator.imageId)
    } yield {
      RunBondedTask(task.id, task.generatorId, filter.id, request.modelId, image.dockerImage)
    }

    job.map {
      job => worker ! job
    }.recover {
      case e: Exception =>
        log.error(e.toString)
        sender ! BondedTaskNotExecutable(request.taskId, e.toString)
    }
  }

  def entry(task: BondedTask, model: ModelUser): Future[Option[Entry]] = {
    val p = Promise[Option[Entry]]

    filterPersistence.read(task.filterId).map { filter =>
      if (filter.instanceIds.contains(model.modelId)) {
        p.success(Some(Entry(task.id, task.menu, task.item)))
      } else {
        p.success(None)
      }
    }.recover {
      case e: Exception =>
        log.error(e.getMessage)
        p.success(None)
    }
    p.future
  }

  def sendBondedTaskList(user: ModelUser): Unit = {
    val allTaskIds = bondedTaskPersistence.readAllIds()
    val allTasks = allTaskIds.flatMap { ids => Future.sequence(ids.map(bondedTaskPersistence.read)) }
    val filteredTasks = allTasks.flatMap(x => Future.sequence(x.map(i => entry(i, user))).map(_.flatten))
    filteredTasks.map(tasks => user.out ! BondedTaskList(tasks.toList))
  }

  def receive: Receive = {
    // handle user request to execute a bonded task
    case request: ExecuteBondedTask => handleRequest(request)
    case request: GetBondedTaskList => sendBondedTaskList(request.user)
  }

}
