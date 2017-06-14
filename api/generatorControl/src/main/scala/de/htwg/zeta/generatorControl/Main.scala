package de.htwg.zeta.generatorControl

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.implicitConversions

import actors.developer.DeveloperManager
import actors.developer.Mediator
import actors.frontend.DeveloperDummy
import actors.master.Master
import actors.worker.DockerWorkExecutor
import actors.worker.Worker
import akka.actor.ActorIdentity
import akka.actor.ActorPath
import akka.actor.ActorSystem
import akka.actor.Identify
import akka.actor.PoisonPill
import akka.actor.Props
import akka.cluster.sharding.ClusterSharding
import akka.cluster.sharding.ClusterShardingSettings
import akka.cluster.singleton.ClusterSingletonManager
import akka.cluster.singleton.ClusterSingletonManagerSettings
import akka.pattern.ask
import akka.persistence.journal.leveldb.SharedLeveldbJournal
import akka.persistence.journal.leveldb.SharedLeveldbStore
import akka.stream.ActorMaterializer
import akka.util.Timeout
import cluster.ClusterManager
import com.typesafe.config.ConfigFactory
import models.session.SyncGatewaySession
import org.rogach.scallop.ScallopConf
import org.rogach.scallop.ScallopOption
import org.slf4j.LoggerFactory
import play.api.libs.ws.ahc.AhcWSClient

object Main extends App {
  val cmd = new Commands(args)

  MasterStarter().initiate(cmd)
  WorkersStarter().initiate(cmd)
  DeveloperStarter().initiate(cmd)
  DummyStarter().initiate(cmd)
}

protected class Commands(arguments: Seq[String]) extends ScallopConf(arguments) {
  val devPort: ScallopOption[Int] = opt[Int]()
  val devSeeds: ScallopOption[List[String]] = opt[List[String]](default = Some(List()))
  val dummyPort: ScallopOption[Int] = opt[Int]()
  val dummySeeds: ScallopOption[List[String]] = opt[List[String]](default = Some(List()))
  val masterNum: ScallopOption[Int] = opt[Int]()
  val masterPort: ScallopOption[Int] = opt[Int]()
  val masterSeeds: ScallopOption[List[String]] = opt[List[String]](default = Some(List()))
  val workers: ScallopOption[Int] = opt[Int]()
  val workerSeeds: ScallopOption[List[String]] = opt[List[String]](default = Some(List()))
  verify()
}

protected trait Starter {
  def initiate(cmd: Commands): Unit

  protected def setSharedJournal(system: ActorSystem, path: ActorPath): Unit = {
    implicit val timeout = Timeout(Duration(15, TimeUnit.SECONDS))
    val f = system.actorSelection(path) ? Identify(None)
    f.onSuccess {
      case ActorIdentity(_, Some(ref)) =>

        SharedLeveldbJournal.setStore(ref, system)
      case _ =>
        system.log.error("Shared journal not started at {}", path)
        system.terminate()
    }
    f.onFailure {
      case _ =>
        system.log.error("Lookup of shared journal at {} timed out", path)
        system.terminate()
    }
  }

  protected def createActorSystem(role: String, seeds: List[String], port: Int): ActorSystem = {
    val roles = List(role)
    val config = ClusterManager.getClusterJoinConfig(roles, seeds, port).withFallback(ConfigFactory.load())
    ActorSystem(Starter.Name, config)
  }
}

protected object Starter {
  val Name = "ClusterSystem"
  /**
   * The time after which a work (execution of a generator, filter, etc..) will be cancelled
   */
  val WorkTimeout: FiniteDuration = Duration(4, TimeUnit.MINUTES)
}

protected class MasterStarter extends Starter {
  case class Config(port: Int, seeds: List[String], num: Int)
  private val logger = LoggerFactory.getLogger(MasterStarter.getClass)

  def initiate(cmd: Commands): Unit = {
    val config = for {
      port <- cmd.masterPort
      seeds <- cmd.masterSeeds
      num <- cmd.masterNum
    } yield Config(port, seeds, num)

    config foreach { start }
  }

  private def start(config: Config): Unit = {
    logger.debug(MasterStarter.LogStart, config.toString)
    if (config.num == 2) {
      Thread.sleep(MasterStarter.MilliSecToWaitForFirstMaster)
    }

    val system = createActor(config)
    startSharedLevelDbOnFirstMaster(config, system)

    val journalAddress = ClusterManager.getJournalPath(config.port, config.seeds)
    logger.debug(MasterStarter.LogJournalAddress, journalAddress.toString)

    setSharedJournal(system, journalAddress)
  }

  private def createActor(config: Config) = {
    implicit val system = createActorSystem(MasterStarter.ActorRole, config.seeds, config.port)
    implicit val mat = ActorMaterializer()
    implicit val client = AhcWSClient()
    val sessionManager = SyncGatewaySession()

    system.actorOf(
      ClusterSingletonManager.props(
        Master.props(MasterStarter.WorkerTimeout, MasterStarter.SessionDuration, sessionManager),
        PoisonPill,
        ClusterSingletonManagerSettings(system).withRole(MasterStarter.ActorRole)
      ),
      MasterStarter.ActorName
    )

    system
  }

  private def startSharedLevelDbOnFirstMaster(config: Config, system: ActorSystem) = {
    if (config.seeds.isEmpty) {
      system.actorOf(Props[SharedLeveldbStore], MasterStarter.ActorSharedLevelDbStore)
    }
  }
}

protected object MasterStarter {
  val ActorName = "master"
  val ActorRole = "backend"
  val ActorSharedLevelDbStore = "store"
  val LogJournalAddress = "Journal Address : {}"
  val LogStart = "Start master actor: {}"
  val MilliSecToWaitForFirstMaster = 10000
  /**
   * This time specify how long the session is, to access the database in a docker container (to execute generator, filter, etc..)
   * Note: This time should be longer as the workTimeout, because if workTimeout was reached, the system should be
   * able to store the log of the docker container
   */
  val SessionDuration: FiniteDuration = Starter.WorkTimeout.plus(Duration(2, TimeUnit.MINUTES))
  /**
   * The time after which a worker will be marked as unreachable
   */
  val WorkerTimeout: FiniteDuration = Duration(5, TimeUnit.MINUTES)

  def apply(): MasterStarter = new MasterStarter()
}

protected class WorkersStarter extends Starter {
  case class Config(numberOfWorkers: Int, seeds: List[String])
  private val logger = LoggerFactory.getLogger(WorkersStarter.getClass)

  def initiate(cmd: Commands): Unit = {
    val config = for {
      workers <- cmd.workers
      seeds <- cmd.workerSeeds
    } yield Config(workers, seeds)

    config foreach { start }
  }

  private def start(config: Config): Unit = {
    Thread.sleep(WorkersStarter.MilliSecWaitForMaster)

    logger.debug(WorkersStarter.LogStart, config.numberOfWorkers, config.toString)
    (1 to config.numberOfWorkers) foreach (i => startWorker(config, i, 0))
  }

  private def startWorker(config: Config, number: Int, port: Int): Unit = {
    val system = createActorSystem(WorkersStarter.ActorRole, config.seeds, port)
    val executor = system.actorOf(DockerWorkExecutor.props(), s"work-executor-$number")
    system.actorOf(Worker.props(executor, WorkersStarter.RegisterInterval, Starter.WorkTimeout), s"worker-$number")
  }
}

protected object WorkersStarter {
  val ActorRole = "worker"
  val LogStart = "Start `{}` worker actor: {}"
  /**
   * The interval in which a worker register itself to the master
   */
  val RegisterInterval: FiniteDuration = Duration(30, TimeUnit.SECONDS)
  val MilliSecWaitForMaster = 20000

  def apply(): WorkersStarter = new WorkersStarter()
}

protected class DummyStarter extends Starter {
  case class Config(port: Int, seeds: List[String])
  private val logger = LoggerFactory.getLogger(DummyStarter.getClass)

  def initiate(cmd: Commands): Unit = {
    val config = for {
      port <- cmd.dummyPort
      seeds <- cmd.dummySeeds
    } yield Config(port, seeds)

    config foreach { start }
  }

  private def start(dummy: Config): Unit = {
    logger.debug(DummyStarter.LogStart, dummy.toString)
    val system = createActorSystem(DummyStarter.ActorRole, dummy.seeds, dummy.port)

    ClusterSharding(system).startProxy(
      typeName = Mediator.shardRegionName,
      role = Some(Mediator.locatedOnNode),
      extractEntityId = Mediator.extractEntityId,
      extractShardId = Mediator.extractShardId
    )

    system.actorOf(DeveloperDummy.props(), DummyStarter.ActorName)
  }
}

protected object DummyStarter {
  val ActorName = "dummy"
  val ActorRole = "dummy"
  val LogStart = "Start dummy actor: {}"

  def apply(): DummyStarter = new DummyStarter()
}

protected class DeveloperStarter extends Starter {
  case class Config(port: Int, seeds: List[String])
  private val logger = LoggerFactory.getLogger(DeveloperStarter.getClass)

  def initiate(cmd: Commands): Unit = {
    val developer = for {
      port <- cmd.devPort
      seeds <- cmd.devSeeds
    } yield Config(port, seeds)

    developer foreach { start }
  }

  private def start(developer: Config): Unit = {
    logger.debug(DeveloperStarter.LogStart, developer.toString)
    Thread.sleep(DeveloperStarter.MilliSecWaitForOtherActors)
    val system = createActorSystem(Mediator.locatedOnNode, developer.seeds, developer.port)

    ClusterSharding(system).start(
      typeName = Mediator.shardRegionName,
      entityProps = Props[Mediator],
      settings = ClusterShardingSettings(system),
      extractEntityId = Mediator.extractEntityId,
      extractShardId = Mediator.extractShardId
    )

    system.actorOf(DeveloperManager.props(), DeveloperStarter.ActorName)

    val journalAddress = ClusterManager.getJournalPath(developer.port, developer.seeds)
    setSharedJournal(system, journalAddress)
  }
}

protected object DeveloperStarter {
  val ActorName = "developer"
  val LogStart = "Start developer actor: {}"
  val MilliSecWaitForOtherActors = 20000
  def apply(): DeveloperStarter = new DeveloperStarter()
}
