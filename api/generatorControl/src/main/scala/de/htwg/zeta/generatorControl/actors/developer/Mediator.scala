package de.htwg.zeta.generatorControl.actors.developer

import java.util.UUID

import scala.collection.mutable

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import akka.cluster.sharding.ShardRegion
import akka.stream.ActorMaterializer
import com.google.inject.Guice
import de.htwg.zeta.common.models.document.Changed
import de.htwg.zeta.common.models.entity.BondedTask
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.frontend.BondedTaskCompleted
import de.htwg.zeta.common.models.frontend.BondedTaskStarted
import de.htwg.zeta.common.models.frontend.CancelWorkByUser
import de.htwg.zeta.common.models.frontend.Connected
import de.htwg.zeta.common.models.frontend.Connection
import de.htwg.zeta.common.models.frontend.CreateGenerator
import de.htwg.zeta.common.models.frontend.DeveloperRequest
import de.htwg.zeta.common.models.frontend.DeveloperResponse
import de.htwg.zeta.common.models.frontend.Disconnected
import de.htwg.zeta.common.models.frontend.ExecuteBondedTask
import de.htwg.zeta.common.models.frontend.GeneratorClient
import de.htwg.zeta.common.models.frontend.GeneratorRequest
import de.htwg.zeta.common.models.frontend.MessageEnvelope
import de.htwg.zeta.common.models.frontend.ModelUser
import de.htwg.zeta.common.models.frontend.Request
import de.htwg.zeta.common.models.frontend.RunFilter
import de.htwg.zeta.common.models.frontend.RunGenerator
import de.htwg.zeta.common.models.frontend.RunGeneratorFromGenerator
import de.htwg.zeta.common.models.frontend.RunModelRelease
import de.htwg.zeta.common.models.frontend.SavedModel
import de.htwg.zeta.common.models.frontend.ToGenerator
import de.htwg.zeta.common.models.frontend.ToolDeveloper
import de.htwg.zeta.common.models.frontend.UserRequest
import de.htwg.zeta.common.models.worker.RunBondedTask
import de.htwg.zeta.generatorControl.actors.developer.manager.BondedTasksManager
import de.htwg.zeta.generatorControl.actors.developer.manager.EventDrivenTasksManager
import de.htwg.zeta.generatorControl.actors.developer.manager.GeneratorConnectionManager
import de.htwg.zeta.generatorControl.actors.developer.manager.GeneratorManager
import de.htwg.zeta.generatorControl.actors.developer.manager.GeneratorRequestManager
import de.htwg.zeta.generatorControl.actors.developer.manager.GetBondedTaskList
import de.htwg.zeta.generatorControl.actors.developer.manager.ManualExecutionManager
import de.htwg.zeta.generatorControl.actors.developer.manager.ModelReleaseManager
import de.htwg.zeta.generatorControl.actors.worker.MasterWorkerProtocol.MasterToDeveloper
import de.htwg.zeta.generatorControl.actors.worker.MasterWorkerProtocol.ToDeveloper
import de.htwg.zeta.generatorControl.actors.worker.MasterWorkerProtocol.WorkerStreamedMessage
import de.htwg.zeta.generatorControl.actors.worker.MasterWorkerProtocol.WorkerToDeveloper
import de.htwg.zeta.persistence.PersistenceModule
import play.api.libs.ws.ahc.AhcWSClient

object Mediator {
  val locatedOnNode = "developer"
  val shardRegionName = "developerMediator"

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case MessageEnvelope(id, message) => (id.toString, message)
  }

  val numberOfShards = 10

  val extractShardId: ShardRegion.ExtractShardId = {
    case MessageEnvelope(id, _) => (id.hashCode % numberOfShards).toString
  }

  def props(): Props = Props(new Mediator())
}

class Mediator() extends Actor with ActorLogging {
  private implicit val mat = ActorMaterializer()
  private implicit val client = AhcWSClient()

  private val mediator: ActorRef = DistributedPubSub(context.system).mediator
  private val developerId: UUID = UUID.fromString(self.path.name)
  mediator ! Subscribe(developerId.toString, self)

  private val injector = Guice.createInjector(new PersistenceModule)

  private val workQueue: ActorRef = context.actorOf(WorkQueue.props(developerId), "workQueue")

  private val generators: ActorRef = context.actorOf(GeneratorManager.props(workQueue, injector), "generators")
  private val modelRelease: ActorRef = context.actorOf(ModelReleaseManager.props(workQueue), "modelRelease")
  private val bondedTasks: ActorRef = context.actorOf(BondedTasksManager.props(workQueue, injector), "bondedTasks")
  private val eventDrivenTasks: ActorRef = context.actorOf(EventDrivenTasksManager.props(workQueue, injector), "eventDrivenTasks")
  private val manualExecution: ActorRef = context.actorOf(ManualExecutionManager.props(workQueue, injector), "manualExecution")
  private val generatorRequest: ActorRef = context.actorOf(GeneratorRequestManager.props(workQueue, injector), "generatorRequest")
  private val generatorConnection: ActorRef = context.actorOf(GeneratorConnectionManager.props(), "generatorConnection")

  private val developers = mutable.Map.empty[UUID, ToolDeveloper]
  private val users = mutable.Map.empty[UUID, ModelUser]

  def receive: Receive = {
    // Handle job messages from the Master or the Worker
    case response: ToDeveloper => processToDeveloper(response)
    // Handle any request from a client to this actor
    case connection: Connection => handleConnection(connection)
    case request: Request => processRequest(request)
    case event: Event => processEvent(event)
    // change from the database
    case changed: Changed => documentChange(changed)
    // Handle any response from this actor to the clients
    case response: DeveloperResponse => sendToToolDeveloperClients(response)
    // error by the workQueue
    case error: JobCannotBeEnqueued => log.warning(error.reason)
  }

  private def processToDeveloper(response: ToDeveloper): Unit = {
    response match {
      case response: MasterToDeveloper => workQueue forward response
      case response: WorkerToDeveloper => handleWorkerResponse(response)
    }
  }

  private def processRequest(request: Request): Unit = {
    request match {
      case request: DeveloperRequest => handleDeveloperRequest(request)
      case request: UserRequest => handleUserRequest(request)
      case request: GeneratorRequest => processGeneratorRequest(request)
    }
  }

  private def processEvent(event: Event): Unit = {
    checkForBondedTask(event)
    generatorConnection ! event
  }

  private def processGeneratorRequest(request: GeneratorRequest): Unit = {
    request match {
      // request which are send from a generator or need to be send to a generator
      case request: RunGeneratorFromGenerator => generatorRequest ! request
      case request: ToGenerator => generatorConnection ! request
    }
  }

  private def documentChange(changed: Changed): Unit = {
    // Handle the update of bonded task lists to users
    changed.doc match {
      case _: BondedTask => resendBondedTasksToUsers()
      case _: Filter => resendBondedTasksToUsers()
      case _ => // other documents can be ignored
    }
  }

  private def handleConnection(connection: Connection): Unit = {
    connection match {
      case Connected(c) => c match {
        case developer: ToolDeveloper =>
          workQueue forward GetJobInfoList
          developers.put(c.id, developer)
        case user: ModelUser =>
          bondedTasks forward GetBondedTaskList(user)
          users.put(c.id, user)
        case _ => generatorConnection ! connection
      }
      case Disconnected(c) => c match {
        case developer: ToolDeveloper => developers.remove(developer.id)
        case user: ModelUser => users.remove(user.id)
        case _: GeneratorClient => generatorConnection ! connection
      }
    }
  }

  private def handleDeveloperRequest(request: DeveloperRequest): Unit = {
    request match {
      case _: CreateGenerator => generators forward request
      case _: RunFilter => manualExecution forward request
      case _: RunGenerator => manualExecution forward request
      case _: RunModelRelease => modelRelease forward request
      case _: CancelWorkByUser => workQueue forward request
    }
  }

  private def handleUserRequest(request: UserRequest): Unit = {
    request match {
      case ExecuteBondedTask(_, _) => bondedTasks forward request
      case SavedModel(_) => eventDrivenTasks forward request
    }
  }

  private def handleWorkerResponse(response: WorkerToDeveloper): Unit = response match {
    case WorkerStreamedMessage(message) => sendToToolDeveloperClients(message)
    case _ =>
  }

  private def sendToToolDeveloperClients(message: Any): Unit = {
    developers foreach {
      case (_, c) =>
        c.out ! message
    }
  }

  private def sendToUserClients(message: Any): Unit = {
    users foreach {
      case (_, c) =>
        c.out ! message
    }
  }

  private def resendBondedTasksToUsers(): Unit = {
    users.foreach {
      case (_, user) =>
        bondedTasks ! GetBondedTaskList(user)
    }
  }

  private def checkForBondedTask(request: Event): Unit = request match {
    case WorkEnqueued(work) => work.job match {
      case RunBondedTask(taskId, _, _, _, _) => sendToUserClients(BondedTaskStarted(taskId))
      case _ => // no bonded task
    }
    case WorkCompleted(work, result) => work.job match {
      case RunBondedTask(taskId, _, _, _, _) => sendToUserClients(BondedTaskCompleted(taskId, result))
      case _ => // no bonded task
    }
    case _ => // no work completed
  }
}
