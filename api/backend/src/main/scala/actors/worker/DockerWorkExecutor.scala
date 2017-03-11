package actors.worker

import java.nio.charset.Charset

import actors.worker.MasterWorkerProtocol.{ CancelWork, Work }
import akka.actor.{ Actor, ActorLogging, Props }
import akka.stream.ActorMaterializer
import com.spotify.docker.client.{ DefaultDockerClient, LogMessage, LogStream }
import com.spotify.docker.client.DockerClient.AttachParameter
import com.spotify.docker.client.exceptions.ContainerNotFoundException
import com.spotify.docker.client.messages.{ ContainerConfig, HostConfig }
import models.document.Log
import models.document.http.{ HttpRepository => DocumentRepository }
import models.frontend.{ JobLog, JobLogMessage }
import play.api.libs.ws.ahc.AhcWSClient
import scala.concurrent.ExecutionContext.Implicits.global

import scala.collection.JavaConversions._
import scala.concurrent._

object DockerWorkExecutor {
  def props() = Props(new DockerWorkExecutor())
}

class DockerWorkExecutor() extends Actor with ActorLogging {
  private val docker = new DefaultDockerClient("unix:///var/run/docker.sock")
  // max number of message to persist
  val maxLogPersistance = 200
  // number of log messages to collect, before sending the messages to the client
  val streamBuffer = 1
  // try to stop the docker container and kill if not stopped after this delay
  val secondsToWaitBeforeKilling = 5

  implicit val mat = ActorMaterializer()
  implicit val client = AhcWSClient()

  private var currentContainerId: Option[String] = None

  override def postStop() = docker.close()

  def receive = {
    case cancelWork: CancelWork => currentContainerId match {
      case Some(id) => {
        log.warning("Received Cancel work {}. Send stop Command to docker.", cancelWork.id)
        try {
          docker.stopContainer(id, secondsToWaitBeforeKilling)
        } catch {
          case e: ContainerNotFoundException => log.warning("Tried to stop already stopped docker container on cancel work {}", cancelWork.id)
          case e: Exception => log.warning("Error on docker stop container {}", e.getMessage)
        }
      }
      case None => {
        log.warning("Tried to stop, but no container is running..")
        sender() ! Worker.WorkComplete(137)
      }
    }

    case work: Work =>
      val documents = DocumentRepository(work.session)
      log.info("DockerWorkExecutor received job {}", work.id)
      var jobStream = JobLog(job = work.id)
      var jobPersist = JobLog(job = work.id)

      val out = sender()

      // send log messages to the client
      def sendMessage(limit: Int) = {
        if (jobStream.messages.length > limit) {
          out ! MasterWorkerProtocol.WorkerStreamedMessage(jobStream.copy())
          jobStream = JobLog(job = work.id)
        }
      }

      def streamMessage(message: JobLogMessage) = {
        jobStream = jobStream.copy(messages = jobStream.messages.enqueue(message))
        sendMessage(streamBuffer)
      }

      def persistMessage(message: JobLogMessage) = {
        // check if max log persistent was reached
        if (jobPersist.messages.length == maxLogPersistance) {
          val error = JobLogMessage("\nStopped to persist log output. Logging should be reduced!\n", "error")
          jobPersist = jobPersist.copy(messages = jobPersist.messages.enqueue(error))
        } else if (jobPersist.messages.size < maxLogPersistance) {
          jobPersist = jobPersist.copy(messages = jobPersist.messages.enqueue(message))
        } else {
          // ignore log messages due to reached maxLogPersistence
        }
      }

      // stream output from the docker container
      def output(logStream: LogStream): Unit = {
        while (logStream.hasNext()) {
          val next = logStream.next()
          val message = Charset.forName("UTF-8").decode(next.content).toString
          val nextMessage = next.stream match {
            case LogMessage.Stream.STDOUT => Some(JobLogMessage(message, "info"))
            case LogMessage.Stream.STDERR => Some(JobLogMessage(message, "error"))
            case LogMessage.Stream.STDIN => None
          }
          nextMessage match {
            case Some(message) => {
              // streaming active?
              if (work.job.stream) {
                streamMessage(message)
              }
              // log persistance active?
              if (work.job.persist) {
                persistMessage(message)
              }
            }
            case None => // no message
          }
        }
      }

      def getExitCodeMessage(status: Int) = {
        if (status == 0) {
          JobLogMessage(s"Exit with status code: ${status}", "info")
        } else {
          JobLogMessage(s"Exit with status code: ${status}", "error")
        }
      }

      def execute(work: Work): Future[Int] = {
        val p = Promise[Int]
        Future {
          try {
            log.info(s"Run image '${work.job.image}' with cmd '${work.job.cmd}'")

            val hostConfig = HostConfig.builder()
              .networkMode("zeta_default")
              .cpuShares(work.dockerSettings.cpuShares)
              .cpuQuota(work.dockerSettings.cpuQuota)
              .build()

            val config = ContainerConfig.builder()
              .hostConfig(hostConfig)
              .image(work.job.image)
              .cmd(work.job.cmd ::: List("--session", work.session) ::: List("--work", work.id))
              .attachStdout(work.job.stream)
              .attachStderr(work.job.stream)
              .hostConfig(hostConfig)
              .build()
            val creation = docker.createContainer(config)
            val id = creation.id

            currentContainerId = Some(id)

            docker.startContainer(id)

            // Redirect stdout and stderr
            val stream = docker.attachContainer(
              id,
              AttachParameter.LOGS, AttachParameter.STDOUT,
              AttachParameter.STDERR, AttachParameter.STREAM
            )

            if (work.job.stream || work.job.persist) {
              output(stream)
            }

            val containerExit = docker.waitContainer(id)
            // no more container is running
            currentContainerId = None

            val status = containerExit.statusCode

            val exit = getExitCodeMessage(status)

            if (work.job.stream) {
              jobStream = jobStream.copy(messages = jobStream.messages.enqueue(exit))
              // Anything not streamed yet?
              sendMessage(0)
            }

            stream.close

            // Remove container
            docker.removeContainer(id)

            if (work.job.persist) {
              jobPersist = jobPersist.copy(messages = jobPersist.messages.enqueue(exit))
              val logs = Log(work.job, jobPersist.toString(), status)

              documents.create[Log](logs)
                .map {
                  result => p.success(status)
                }
                .recover {
                  case e: Exception => {
                    log.warning(s"Exception on saving log from docker execution ${e.getMessage}")
                    p.failure(e)
                  }
                }
            } else {
              p.success(status)
            }
          } catch {
            case e: Exception =>
              log.warning(s"Exception on docker execution ${e.getMessage}")
              log.error(e.getCause.toString)
              p.failure(e)
          }
        }(ExecutionContext.Implicits.global)

        p.future
      }

      execute(work).map {
        result => out ! Worker.WorkComplete(result)
      } recover {
        case e: Exception => {
          if (work.job.stream) {
            jobStream = jobStream.copy(messages = jobStream.messages.enqueue(JobLogMessage(e.getCause.toString, "error")))
            out ! MasterWorkerProtocol.WorkerStreamedMessage(jobStream)
          }
          out ! Worker.WorkComplete(1)
        }
      }
  }
}