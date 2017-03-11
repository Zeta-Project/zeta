package actors.developer

import actors.common._
import actors.developer.Mediator.Refresh
import actors.developer.WorkQueue.JobCannotBeEnqueued
import actors.developer.WorkState._
import actors.developer.manager._
import actors.worker.MasterWorkerProtocol._
import akka.actor.{ Actor, ActorLogging, Props }
import akka.cluster.pubsub.{ DistributedPubSub, DistributedPubSubMediator }
import akka.cluster.sharding.ShardRegion
import akka.stream.ActorMaterializer
import models.document._
import models.document.http.{ CachedRepository, HttpRepository }
import models.frontend._
import models.session.SyncGatewaySession
import models.worker._
import play.api.libs.ws.ahc.AhcWSClient

import scala.concurrent.duration._
import scala.concurrent.Await

object Mediator {
  val locatedOnNode = "developer"
  val shardRegionName = "developerMediator"

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case MessageEnvelope(id, message) => (id, message)
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

  import context.dispatcher
  import DistributedPubSubMediator.{ Subscribe }
  val mediator = DistributedPubSub(context.system).mediator
  val developerId = self.path.name
  mediator ! Subscribe(developerId, self)

  // Get a session from the database. Blocking should be ok here because we need to initialize the actor
  // and if we don't get a session from the database we should stop the actor
  val sessionManager = SyncGatewaySession()
  val session = Await.result(sessionManager.getSession(developerId, ttl), 10.seconds)
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

  var developers: Map[String, ToolDeveloper] = Map()
  var users: Map[String, ModelUser] = Map()

  val registerTask = context.system.scheduler.schedule((ttl / 10).seconds, (ttl / 10).seconds, self, Refresh)

  override def postStop() = {
    registerTask.cancel()
  }

  def refreshSession = {
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
    case response: MasterToDeveloper => workQueue forward response
    case response: WorkerToDeveloper => handleWorkerResponse(response)
    // Handle any request from a client to this actor
    case connection: Connection => handleConnection(connection)
    case request: DeveloperRequest => handleDeveloperRequest(request)
    case request: UserRequest => handleUserRequest(request)
    // request which are send from a generator or need to be send to a generator
    case request: RunGeneratorFromGenerator => generatorRequest ! request
    case request: ToGenerator => generatorConnection ! request
    case request: WorkState.Event => {
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
          workQueue forward WorkQueue.GetJobInfoList
          developers += (client.id -> developer)
        }
        case user @ ModelUser(out, id, model) => {
          bondedTasks forward BondedTasksManager.GetBondedTaskList(user)
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
        bondedTasks ! BondedTasksManager.GetBondedTaskList(user)
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
