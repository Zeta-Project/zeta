package actors.developer

import java.util.UUID
import java.util.concurrent.TimeUnit

import actors.common.AllDocsFromDeveloper
import actors.common.ChangeFeed
import actors.common.Configuration
import actors.common.Images
import actors.developer.Mediator.Refresh
import actors.developer.manager.BondedTasksManager
import actors.developer.manager.EventDrivenTasksManager
import actors.developer.manager.FiltersManager
import actors.developer.manager.GeneratorConnectionManager
import actors.developer.manager.GeneratorRequestManager
import actors.developer.manager.GeneratorsManager
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
import models.document.BondedTask
import models.document.Changed
import models.document.Filter
import models.document.http.CachedRepository
import models.document.http.HttpRepository
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
import models.session.SyncGatewaySession
import models.worker.RunBondedTask
import play.api.libs.ws.ahc.AhcWSClient
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

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

  private case object Refresh
}

class Mediator() extends Actor with ActorLogging {
  implicit val mat = ActorMaterializer()
  implicit val client = AhcWSClient()

  // the ttl of the session to access the database
  val ttl = 3600

  val mediator = DistributedPubSub(context.system).mediator
  val developerId = self.path.name
  mediator ! Subscribe(developerId, self)

  // Get a session from the database. Blocking should be ok here because we need to initialize the actor
  // and if we don't get a session from the database we should stop the actor
  val sessionManager = SyncGatewaySession()
  val session = Await.result(sessionManager.getSession(developerId, ttl), Duration(10, TimeUnit.SECONDS))
  val remote = HttpRepository(session)
  val repository = CachedRepository(remote)

  val workQueue = context.actorOf(WorkQueue.props(developerId), "workQueue")

  val filters = context.actorOf(FiltersManager.props(workQueue, repository), "filters")
  val generators = context.actorOf(GeneratorsManager.props(workQueue, repository), "generators")
  val modelRelease = context.actorOf(ModelReleaseManager.props(workQueue), "modelRelease")
  val bondedTasks = context.actorOf(BondedTasksManager.props(workQueue, repository), "bondedTasks")
  val eventDrivenTasks = context.actorOf(EventDrivenTasksManager.props(workQueue, repository), "eventDrivenTasks")
  val timedTasks = context.actorOf(TimedTasksManager.props(workQueue, repository), "timedTasks")
  val manualExecution = context.actorOf(ManualExecutionManager.props(workQueue, repository), "manuealExeuction")
  val generatorRequest = context.actorOf(GeneratorRequestManager.props(workQueue, repository), "generatorRequest")
  val generatorConnection = context.actorOf(GeneratorConnectionManager.props(), "generatorConnection")

  val conf = Configuration()
  val channels = List(AllDocsFromDeveloper(developerId), Images())
  val listeners = List(self, bondedTasks, eventDrivenTasks, timedTasks, manualExecution, modelRelease, filters, generators, workQueue)
  val changeFeed = context.actorOf(ChangeFeed.props(conf, channels, listeners))

  var developers: Map[UUID, ToolDeveloper] = Map()
  var users: Map[UUID, ModelUser] = Map()

  val registerTask = context.system.scheduler.schedule(Duration(ttl / 10, TimeUnit.SECONDS), Duration(ttl / 10, TimeUnit.SECONDS), self, Refresh)

  override def postStop() = {
    registerTask.cancel()
  }

  private def refreshSession() = {
    sessionManager.getSession(developerId, ttl).map { session =>
      remote.session = session
    }.recover {
      case e: Exception => log.error(e.getMessage)
    }
  }

  def receive: Receive = {
    // refresh the session to access the db
    case Refresh => refreshSession
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
    repository.invalidate(changed.doc.id())
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
      case RunBondedTask(task, generator, filter, model, image) => sendToUserClients(BondedTaskStarted(task))
      case _ => // no bonded task
    }
    case WorkCompleted(work, result) => work.job match {
      case RunBondedTask(task, generator, filter, model, image) => sendToUserClients(BondedTaskCompleted(task, result))
      case _ => // no bonded task
    }
    case _ => // no work completed
  }
}
