package actors.developer

import java.util.UUID

import actors.developer.manager.BondedTasksManager
import actors.developer.manager.EventDrivenTasksManager
import actors.developer.manager.FiltersManager
import actors.developer.manager.GeneratorConnectionManager
import actors.developer.manager.GeneratorManager
import actors.developer.manager.GeneratorRequestManager
import actors.developer.manager.GetBondedTaskList
import actors.developer.manager.ManualExecutionManager
import actors.developer.manager.ModelReleaseManager
import actors.developer.manager.TimedTasksManager
import actors.worker.MasterWorkerProtocol.MasterToDeveloper
import actors.worker.MasterWorkerProtocol.ToDeveloper
import actors.worker.MasterWorkerProtocol.WorkerStreamedMessage
import actors.worker.MasterWorkerProtocol.WorkerToDeveloper
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import akka.cluster.sharding.ShardRegion
import akka.stream.ActorMaterializer
import de.htwg.zeta.persistence.Persistence
import models.document.Changed
import models.entity.BondedTask
import models.entity.Filter
import models.frontend.BondedTaskCompleted
import models.frontend.BondedTaskStarted
import models.frontend.CancelWorkByUser
import models.frontend.Connected
import models.frontend.Connection
import models.frontend.CreateGenerator
import models.frontend.DeveloperRequest
import models.frontend.DeveloperResponse
import models.frontend.Disconnected
import models.frontend.ExecuteBondedTask
import models.frontend.GeneratorClient
import models.frontend.GeneratorRequest
import models.frontend.MessageEnvelope
import models.frontend.ModelUser
import models.frontend.Request
import models.frontend.RunFilter
import models.frontend.RunGenerator
import models.frontend.RunGeneratorFromGenerator
import models.frontend.RunModelRelease
import models.frontend.SavedModel
import models.frontend.ToGenerator
import models.frontend.ToolDeveloper
import models.frontend.UserRequest
import models.worker.RunBondedTask
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

  def props() = Props(new Mediator())

}

class Mediator() extends Actor with ActorLogging {
  implicit val mat = ActorMaterializer()
  implicit val client = AhcWSClient()

  // the ttl of the session to access the database
  val ttl = 3600

  val mediator = DistributedPubSub(context.system).mediator
  val developerId = UUID.fromString(self.path.name)
  mediator ! Subscribe(developerId.toString, self)

  val repo = Persistence.restrictedAccessRepository(developerId)

  val workQueue = context.actorOf(WorkQueue.props(developerId), "workQueue")

  val filters = context.actorOf(FiltersManager.props(workQueue, repo), "filters")
  val generators = context.actorOf(GeneratorManager.props(workQueue, repo), "generators")
  val modelRelease = context.actorOf(ModelReleaseManager.props(workQueue), "modelRelease")
  val bondedTasks = context.actorOf(BondedTasksManager.props(workQueue, repo), "bondedTasks")
  val eventDrivenTasks = context.actorOf(EventDrivenTasksManager.props(workQueue, repo), "eventDrivenTasks")
  val timedTasks = context.actorOf(TimedTasksManager.props(workQueue, repo), "timedTasks")
  val manualExecution = context.actorOf(ManualExecutionManager.props(workQueue, repo), "manualExecution")
  val generatorRequest = context.actorOf(GeneratorRequestManager.props(workQueue, repo), "generatorRequest")
  val generatorConnection = context.actorOf(GeneratorConnectionManager.props(), "generatorConnection")


  val listeners = List(self, bondedTasks, eventDrivenTasks, timedTasks, manualExecution, modelRelease, filters, generators, workQueue)


  var developers: Map[UUID, ToolDeveloper] = Map()
  var users: Map[UUID, ModelUser] = Map()




  def receive: Receive = {
    // Handle job messages from the Master or the Worker
    case response: ToDeveloper => processToDeveloper(response)
    // Handle any request from a client to this actor
    case connection: Connection => handleConnection(connection)
    case request: Request => processRequest(request)
    case request: Event => {
      checkForBondedTask(request)
      generatorConnection ! request
    }
    // change from the database
    case changed: Changed => documentChange(changed)
    // Handle any response from this actor to the clients
    case response: DeveloperResponse => sendToToolDeveloperClients(response)
    // error by the workQueue
    case error: JobCannotBeEnqueued => log.warning(error.reason)
  }

  private def processToDeveloper(response: ToDeveloper) = {
    response match {
      case response: MasterToDeveloper => workQueue forward response
      case response: WorkerToDeveloper => handleWorkerResponse(response)
    }
  }

  private def processRequest(request: Request) = {
    request match {
      case request: DeveloperRequest => handleDeveloperRequest(request)
      case request: UserRequest => handleUserRequest(request)
      case request: GeneratorRequest => processGeneratorRequest(request)
    }
  }

  private def processGeneratorRequest(request: GeneratorRequest) = {
    request match {
      // request which are send from a generator or need to be send to a generator
      case request: RunGeneratorFromGenerator => generatorRequest ! request
      case request: ToGenerator => generatorConnection ! request
    }
  }

  def documentChange(changed: Changed) = {
    // Handle the update of bonded task lists to users
    changed.doc match {
      case _: BondedTask => resendBondedTasksToUsers
      case _: Filter => resendBondedTasksToUsers
      case _ => // other documents can be ignored
    }
  }

  def handleConnection(connection: Connection) = {
    connection match {
      case Connected(client) => client match {
        case developer @ ToolDeveloper(out, user) => {
          workQueue forward GetJobInfoList
          developers += (client.id -> developer)
        }
        case user @ ModelUser(out, id, model) => {
          bondedTasks forward GetBondedTaskList(user)
          users += (client.id -> user)
        }
        case _ => generatorConnection ! connection
      }
      case Disconnected(client) => client match {
        case developer @ ToolDeveloper(out, user) => developers -= developer.id
        case user @ ModelUser(out, id, model) => users -= user.id
        case generator @ GeneratorClient(out, id) => generatorConnection ! connection
      }
    }
  }

  def handleDeveloperRequest(request: DeveloperRequest) = {
    request match {
      case _: CreateGenerator => generators forward request
      case _: RunFilter => manualExecution forward request
      case _: RunGenerator => manualExecution forward request
      case _: RunModelRelease => modelRelease forward request
      case _: CancelWorkByUser => workQueue forward request
    }
  }

  def handleUserRequest(request: UserRequest) = {
    request match {
      case ExecuteBondedTask(_, _) => bondedTasks forward request
      case SavedModel(_) => eventDrivenTasks forward request
    }
  }

  def handleWorkerResponse(response: WorkerToDeveloper) = response match {
    case WorkerStreamedMessage(message) => sendToToolDeveloperClients(message)
    case _ =>
  }

  def sendToToolDeveloperClients(message: Any) = {
    developers foreach {
      case (id, client) =>
        client.out ! message
    }
  }

  def sendToUserClients(message: Any) = {
    users foreach {
      case (id, client) =>
        client.out ! message
    }
  }

  def resendBondedTasksToUsers() = {
    users.foreach {
      case (id, user) =>
        bondedTasks ! GetBondedTaskList(user)
    }
  }

  def checkForBondedTask(request: Event) = request match {
    case WorkEnqueued(work) => work.job match {
      case RunBondedTask(taskId, generatorId, filterId, modelId, image) => sendToUserClients(BondedTaskStarted(taskId))
      case _ => // no bonded task
    }
    case WorkCompleted(work, result) => work.job match {
      case RunBondedTask(taskId, generatorId, filterId, modelId, image) => sendToUserClients(BondedTaskCompleted(taskId, result))
      case _ => // no bonded task
    }
    case _ => // no work completed
  }
}
