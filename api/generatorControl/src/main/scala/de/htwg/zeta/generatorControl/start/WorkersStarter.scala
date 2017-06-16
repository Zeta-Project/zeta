package de.htwg.zeta.generatorControl.start

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

import de.htwg.zeta.generatorControl.actors.worker.DockerWorkExecutor
import de.htwg.zeta.generatorControl.actors.worker.Worker
import org.slf4j.LoggerFactory

/**
 */
class WorkersStarter(config: WorkerConfig) extends Starter {

  private val logger = LoggerFactory.getLogger(WorkersStarter.getClass)

  def start(): Unit = {
    Thread.sleep(WorkersStarter.MilliSecWaitForMaster)

    logger.debug(WorkersStarter.LogStart, config.numberOfWorkers, config.toString)
    (1 to config.numberOfWorkers) foreach (i => startWorker(config, i, 0))
  }

  private def startWorker(config: WorkerConfig, number: Int, port: Int): Unit = {
    val system = createActorSystem(WorkersStarter.ActorRole, config.seeds, port)
    val executor = system.actorOf(DockerWorkExecutor.props(), s"work-executor-$number")
    system.actorOf(Worker.props(executor, WorkersStarter.RegisterInterval, Starter.WorkTimeout), s"worker-$number")
  }
}

object WorkersStarter {
  val ActorRole = "worker"
  val LogStart = "Start `{}` worker actor: {}"
  /**
   * The interval in which a worker register itself to the master
   */
  val RegisterInterval: FiniteDuration = Duration(30, TimeUnit.SECONDS)
  val MilliSecWaitForMaster = 20000

  def apply(cmd: Commands): Option[WorkersStarter] = {
    val config = for {
      workers <- cmd.workers
      seeds <- cmd.workerSeeds
    } yield {
      WorkerConfig(workers, seeds)
    }

    config.toOption.map(new WorkersStarter(_))
  }

}

case class WorkerConfig(numberOfWorkers: Int, seeds: List[String])
