package de.htwg.zeta.generatorControl.actors.worker

import java.nio.charset.Charset
import java.util.UUID

import scala.collection.JavaConversions.asScalaIterator
import scala.collection.JavaConversions.seqAsJavaList
import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.event.LoggingAdapter
import akka.stream.ActorMaterializer
import com.google.inject.Guice
import com.spotify.docker.client.DefaultDockerClient
import com.spotify.docker.client.DockerClient
import com.spotify.docker.client.DockerClient.AttachParameter
import com.spotify.docker.client.LogMessage
import com.spotify.docker.client.LogStream
import com.spotify.docker.client.exceptions.ContainerNotFoundException
import com.spotify.docker.client.messages.ContainerConfig
import com.spotify.docker.client.messages.HostConfig
import com.typesafe.config.ConfigFactory
import de.htwg.zeta.common.models.entity.Entity
import de.htwg.zeta.common.models.entity.Log
import de.htwg.zeta.common.models.entity.MetaModelRelease
import de.htwg.zeta.common.models.frontend.JobLog
import de.htwg.zeta.common.models.frontend.JobLogMessage
import de.htwg.zeta.generatorControl.actors.worker.MasterWorkerProtocol.CancelWork
import de.htwg.zeta.generatorControl.actors.worker.MasterWorkerProtocol.Work
import de.htwg.zeta.persistence.PersistenceModule
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedEntityPersistence
import de.htwg.zeta.persistence.general.EntityRepository
import org.joda.time.DateTime
import play.api.libs.ws.ahc.AhcWSClient

object DockerWorkExecutor {
  private[worker] val messageStart = "DockerWorkExecutor - started"
  private[worker] val messageCancelWork = "DockerWorkExecutor - Received Cancel work {}. Send stop Command to docker."
  private[worker] val messageAlreadyStopped = "DockerWorkExecutor - {}"
  private[worker] val messageStopFailed = "DockerWorkExecutor - Error on docker stop container {}"
  private[worker] val messageNothingToCancel = "DockerWorkExecutor - Tried to stop, but no container is running.. 'workId: {}'"
  private[worker] val messageReceivedJob = "DockerWorkExecutor - Received job {}"
  private[worker] val messageExecute = "DockerWorkExecutor - Execute container for job {}"
  private[worker] val messageRun = "DockerWorkExecutor - Run image '{}' with cmd '{}' for job {}"
  private[worker] val messageRunError = "DockerWorkExecutor - Exception on container execution: '{}' '{}'"
  private[worker] val messageSaveLogsError = "DockerWorkExecutor - Exception on saving log from docker execution {}"
  private[worker] val messageConfig = "DockerWorkExecutor - Container Config '{}'"
  private[worker] val messageCreateLogError = "DockerWorkExecutor - Creating Log(..) Object failed. Job type '%s' cannot be persisted."
  private[worker] val messageExecuteFailed = "DockerWorkExecutor - Execute '{}' failed: '{}' '{}'"
  private[worker] val exitFailure = 0
  private[worker] val exitJvmBySigkill = 137

  // max number of message to persist
  private[worker] val maxLogPersistance = 200
  // number of log messages to collect, before sending the messages to the client
  private[worker] val streamBuffer = 1
  // try to stop the docker container and kill if not stopped after this delay
  private[worker] val secondsToWaitBeforeKilling = 5

  def props(): Props = Props(new DockerWorkExecutor())
}

class DockerWorkExecutor() extends Actor with ActorLogging {
  private val docker = new DefaultDockerClient("unix:///var/run/docker.sock")

  implicit val mat = ActorMaterializer()
  implicit val client = AhcWSClient()

  private val currentContainer = mutable.HashMap.empty[String, String]

  log.info(DockerWorkExecutor.messageStart)

  override def postStop(): Unit = docker.close()

  def receive: Receive = {
    case cancelWork: CancelWork => processCancelWork(cancelWork.id)
    case work: Work => new WorkProcessor(work, docker, sender(), log, currentContainer)
  }

  private def processCancelWork(workId: String): Unit = {
    currentContainer.remove(workId) match {
      case Some(containerId) => stopContainer(containerId)
      case None =>
        log.warning(DockerWorkExecutor.messageNothingToCancel, workId)
        sender() ! WorkComplete(DockerWorkExecutor.exitJvmBySigkill)
    }
  }

  private def stopContainer(containerId: String) = {
    log.warning(DockerWorkExecutor.messageCancelWork, containerId)
    try {
      docker.stopContainer(containerId, DockerWorkExecutor.secondsToWaitBeforeKilling)
    } catch {
      case e: ContainerNotFoundException => log.warning(DockerWorkExecutor.messageAlreadyStopped, e.getMessage)
      case e: Exception => log.warning(DockerWorkExecutor.messageStopFailed, e.getMessage)
    }
  }
}

private class WorkProcessor(
    work: Work,
    docker: DockerClient,
    out: ActorRef,
    log: LoggingAdapter,
    currentContainer: mutable.Map[String, String]
  ) {

  log.info(DockerWorkExecutor.messageReceivedJob, work.id)

  private val injector = Guice.createInjector(new PersistenceModule)
  private val logPersistence = injector.getInstance(classOf[AccessRestrictedEntityPersistence[Log]]).restrictedTo(work.owner)
  private val metaModelReleasePersistence = injector.getInstance(classOf[EntityRepository[MetaModelRelease]])

  private var jobStream = JobLog(job = work.id)
  private var jobPersist = JobLog(job = work.id)

  execute(work).map {
    result => out ! WorkComplete(result)
  } recover {
    case e: Exception =>
      if (work.job.stream) {
        log.error(DockerWorkExecutor.messageExecuteFailed, work.id, e.getMessage, e.getCause.toString)
        jobStream = jobStream.copy(messages = jobStream.messages.enqueue(JobLogMessage(e.getCause.toString, "error")))
        out ! MasterWorkerProtocol.WorkerStreamedMessage(jobStream)
      }
      out ! WorkComplete(DockerWorkExecutor.exitFailure)
  }

  // send log messages to the client
  private def sendMessage(limit: Int): Unit = {
    if (jobStream.messages.length > limit) {
      out ! MasterWorkerProtocol.WorkerStreamedMessage(jobStream.copy())
      jobStream = JobLog(job = work.id)
    }
  }

  private def streamMessage(message: JobLogMessage): Unit = {
    jobStream = jobStream.copy(messages = jobStream.messages.enqueue(message))
    sendMessage(DockerWorkExecutor.streamBuffer)
  }

  private def persistMessage(message: JobLogMessage): Unit = {
    // check if max log persistent was reached
    if (jobPersist.messages.length == DockerWorkExecutor.maxLogPersistance) {
      val error = JobLogMessage("\nStopped to persist log output. Logging should be reduced!\n", "error")
      jobPersist = jobPersist.copy(messages = jobPersist.messages.enqueue(error))
    } else if (jobPersist.messages.size < DockerWorkExecutor.maxLogPersistance) {
      jobPersist = jobPersist.copy(messages = jobPersist.messages.enqueue(message))
    } else {
      // ignore log messages due to reached maxLogPersistence
    }
  }

  // stream output from the docker container
  private def output(logStream: LogStream): Unit = {
    for {next <- logStream} {
      val message = Charset.forName("UTF-8").decode(next.content).toString
      val nextMessage = next.stream match {
        case LogMessage.Stream.STDOUT => Some(JobLogMessage(message, "info"))
        case LogMessage.Stream.STDERR => Some(JobLogMessage(message, "error"))
        case LogMessage.Stream.STDIN => None
      }
      nextMessage match {
        case Some(msg) =>
          // streaming active?
          if (work.job.stream) {
            streamMessage(msg)
          }
          // log persistance active?
          if (work.job.persist) {
            persistMessage(msg)
          }
        case None => // no message
      }
    }
  }

  private def getExitCodeMessage(status: Int): JobLogMessage = {
    if (status == 0) {
      JobLogMessage(s"Exit with status code: $status", "info")
    } else {
      JobLogMessage(s"Exit with status code: $status", "error")
    }
  }

  private def execute(work: Work): Future[Int] = {
    log.info(DockerWorkExecutor.messageExecute, work.id)
    val p = Promise[Int]
    Future {
      try {
        runContainer(p)
      } catch {
        case e: Exception =>
          log.error(DockerWorkExecutor.messageRunError, e.getMessage, e.getCause.toString)
          p.failure(e)
      }
    }(ExecutionContext.Implicits.global)

    p.future
  }

  private def runContainer(p: Promise[Int]) = {
    log.info(DockerWorkExecutor.messageRun, work.job.image, work.job.cmd, work.id)

    val id = createContainer()
    currentContainer(work.id) = id
    docker.startContainer(id)

    val stream = processStream(id)
    val containerExit = docker.waitContainer(id)
    // no more container is running
    currentContainer.remove(work.id)

    val status = containerExit.statusCode

    val exit = getExitCodeMessage(status)

    if (work.job.stream) {
      jobStream = jobStream.copy(messages = jobStream.messages.enqueue(exit))
      // Anything not streamed yet?
      sendMessage(0)
    }
    stream.close()

    // Remove container
    docker.removeContainer(id)

    if (work.job.persist) processLogs(p, status, exit) else p.success(status)
  }

  private def createContainer(): String = {
    val hostConfig = createHostConfig()
    val config = createContainerConfig(hostConfig)
    log.info(DockerWorkExecutor.messageConfig, config.toString)
    val creation = docker.createContainer(config)
    creation.id
  }

  private def createHostConfig(): HostConfig = {
    HostConfig.builder()
      .networkMode("zeta_default")
      .cpuShares(work.dockerSettings.cpuShares)
      .cpuQuota(work.dockerSettings.cpuQuota)
      .build()
  }

  private def createContainerConfig(hostConfig: HostConfig): ContainerConfig = {
    ContainerConfig.builder()
      .hostConfig(hostConfig)
      .image(work.job.image)
      .cmd(work.job.cmd ::: List("--session", work.owner.toString) ::: List("--work", work.id))
      .attachStdout(work.job.stream)
      .attachStderr(work.job.stream)
      .env(buildEnv())
      .hostConfig(hostConfig)
      .build()
  }

  private def buildEnv(): List[String] = {
    val config = ConfigFactory.load()
    List(
      s"ZETA_MONGODB_SERVER=${config.getString("zeta.mongodb.server")}",
      s"ZETA_MONGODB_PORT=${config.getInt("zeta.mongodb.port")}",
      s"ZETA_MONGODB_DB=${config.getString("zeta.mongodb.db")}",
      s"ZETA_MONGODB_USERNAME=${config.getString("zeta.mongodb.username")}",
      s"ZETA_MONGODB_PASSWORD=${config.getString("zeta.mongodb.password")}"
    )
  }

  private def processStream(id: String): LogStream = {
    // Redirect stdout and stderr
    val stream = docker.attachContainer(
      id,
      AttachParameter.LOGS, AttachParameter.STDOUT,
      AttachParameter.STDERR, AttachParameter.STREAM
    )

    if (work.job.stream || work.job.persist) {
      output(stream)
    }

    stream
  }

  private def processLogs(p: Promise[Int], status: Integer, exit: JobLogMessage): Future[Any] = {
    val now = new DateTime().toDateTimeISO.toString
    jobPersist = jobPersist.copy(messages = jobPersist.messages.enqueue(exit))
    val task = work.job match {
      case job: Entity =>
        "Log - " + job.id.toString + " - " + now
      case _ =>
        val message = DockerWorkExecutor.messageCreateLogError.format(work.job.getClass.getName)
        throw new IllegalArgumentException(message)
    }

    val logs = Log(UUID.randomUUID, task.toString, "Log" + task, status, now)

    logPersistence.create(logs).map {
      _ => p.success(status)
    }.recover {
      case e: Exception =>
        log.error(DockerWorkExecutor.messageSaveLogsError, e.getMessage)
        p.failure(e)
    }
  }
}
