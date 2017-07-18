package de.htwg.zeta.generatorControl.actors.developer

import java.util.UUID

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import akka.cluster.sharding.ShardRegion
import akka.stream.ActorMaterializer
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
import de.htwg.zeta.generatorControl.actors.developer.manager.FiltersManager
import de.htwg.zeta.generatorControl.actors.developer.manager.GeneratorConnectionManager
import de.htwg.zeta.generatorControl.actors.developer.manager.GeneratorManager
import de.htwg.zeta.generatorControl.actors.developer.manager.GeneratorRequestManager
import de.htwg.zeta.generatorControl.actors.developer.manager.GetBondedTaskList
import de.htwg.zeta.generatorControl.actors.developer.manager.ManualExecutionManager
import de.htwg.zeta.generatorControl.actors.developer.manager.ModelReleaseManager
import de.htwg.zeta.generatorControl.actors.developer.manager.TimedTasksManager
import de.htwg.zeta.generatorControl.actors.worker.MasterWorkerProtocol.MasterToDeveloper
import de.htwg.zeta.generatorControl.actors.worker.MasterWorkerProtocol.ToDeveloper
import de.htwg.zeta.generatorControl.actors.worker.MasterWorkerProtocol.WorkerStreamedMessage
import de.htwg.zeta.generatorControl.actors.worker.MasterWorkerProtocol.WorkerToDeveloper
import de.htwg.zeta.persistence.Persistence
import de.htwg.zeta.persistence.general.Repository
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

  val mediator: ActorRef = DistributedPubSub(context.system).mediator
  val developerId: UUID = UUID.fromString(self.path.name)
  mediator ! Subscribe(developerId.toString, self)

  val repo: Repository = Persistence.restrictedAccessRepository(developerId)

  val workQueue: ActorRef = context.actorOf(WorkQueue.props(developerId), "workQueue")

  val filters: ActorRef = context.actorOf(FiltersManager.props(workQueue, repo), "filters")
  val generators: ActorRef = context.actorOf(GeneratorManager.props(workQueue, Persistence.fullAccessRepository), "generators")
  val modelRelease: ActorRef = context.actorOf(ModelReleaseManager.props(workQueue), "modelRelease")
  val bondedTasks: ActorRef = context.actorOf(BondedTasksManager.props(workQueue, repo), "bondedTasks")
  val eventDrivenTasks: ActorRef = context.actorOf(EventDrivenTasksManager.props(workQueue, repo), "eventDrivenTasks")
  val timedTasks: ActorRef = context.actorOf(TimedTasksManager.props(workQueue, repo), "timedTasks")
  val manualExecution: ActorRef = context.actorOf(ManualExecutionManager.props(workQueue, repo), "manualExecution")
  val generatorRequest: ActorRef = context.actorOf(GeneratorRequestManager.props(workQueue, repo), "generatorRequest")
  val generatorConnection: ActorRef = context.actorOf(GeneratorConnectionManager.props(), "generatorConnection")


  val listeners = List(self, bondedTasks, eventDrivenTasks, timedTasks, manualExecution, modelRelease, filters, generators, workQueue)


  var developers: Map[UUID, ToolDeveloper] = Map()
  var users: Map[UUID, ModelUser] = Map()




  def receive: Receive = {
    // Handle job messages from the Master or the Worker
    case response: ToDeveloper => processToDeveloper(response)
    // Handle any request from a client to this actor
    case connection: Connection => handleConnection(connection)
    case request: Request => processRequest(request)
    case request: Event =>
      checkForBondedTask(request)
      generatorConnection ! request
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

  private def processGeneratorRequest(request: GeneratorRequest): Unit = {
    request match {
      // request which are send from a generator or need to be send to a generator
      case request: RunGeneratorFromGenerator => generatorRequest ! request
      case request: ToGenerator => generatorConnection ! request
    }
  }

  def documentChange(changed: Changed): Unit = {
    // Handle the update of bonded task lists to users
    changed.doc match {
      case _: BondedTask => resendBondedTasksToUsers()
      case _: Filter => resendBondedTasksToUsers()
      case _ => // other documents can be ignored
    }
  }

  def handleConnection(connection: Connection): Unit = {
    connection match {
      case Connected(c) => c match {
        case developer @ ToolDeveloper(out, user) =>
          workQueue forward GetJobInfoList
          developers += (c.id -> developer)
        case user @ ModelUser(out, id, model) =>
          bondedTasks forward GetBondedTaskList(user)
          users += (c.id -> user)
        case _ => generatorConnection ! connection
      }
      case Disconnected(c) => c match {
        case developer @ ToolDeveloper(out, user) => developers -= developer.id
        case user @ ModelUser(out, id, model) => users -= user.id
        case generator @ GeneratorClient(out, id) => generatorConnection ! connection
      }
    }
  }

  def handleDeveloperRequest(request: DeveloperRequest): Unit = {
    request match {
      case _: CreateGenerator => generators forward request
      case _: RunFilter => manualExecution forward request
      case _: RunGenerator => manualExecution forward request
      case _: RunModelRelease => modelRelease forward request
      case _: CancelWorkByUser => workQueue forward request
    }
  }

  def handleUserRequest(request: UserRequest): Unit = {
    request match {
      case ExecuteBondedTask(_, _) => bondedTasks forward request
      case SavedModel(_) => eventDrivenTasks forward request
    }
  }

  def handleWorkerResponse(response: WorkerToDeveloper): Unit = response match {
    case WorkerStreamedMessage(message) => sendToToolDeveloperClients(message)
    case _ =>
  }

  def sendToToolDeveloperClients(message: Any): Unit = {
    developers foreach {
      case (id, c) =>
        c.out ! message
    }
  }

  def sendToUserClients(message: Any): Unit = {
    users foreach {
      case (id, c) =>
        c.out ! message
    }
  }

  def resendBondedTasksToUsers(): Unit = {
    users.foreach {
      case (id, user) =>
        bondedTasks ! GetBondedTaskList(user)
    }
  }

  def checkForBondedTask(request: Event): Unit = request match {
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
