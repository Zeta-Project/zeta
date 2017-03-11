import actors.developer.{ DeveloperManager, Mediator }
import actors.frontend.DeveloperDummy
import actors.master.Master
import actors.worker.{ DockerWorkExecutor, DummyWorkerExecutor, Worker }
import akka.actor.{ ActorIdentity, ActorPath, ActorSystem, Identify, PoisonPill, Props }
import akka.cluster.sharding.{ ClusterSharding, ClusterShardingSettings }
import akka.cluster.singleton.{ ClusterSingletonManager, ClusterSingletonManagerSettings }
import akka.persistence.journal.leveldb.{ SharedLeveldbJournal, SharedLeveldbStore }
import akka.util.Timeout
import akka.pattern.ask
import akka.stream.ActorMaterializer
import cluster.ClusterManager
import com.typesafe.config.ConfigFactory
import models.session.SyncGatewaySession
import org.rogach.scallop.ScallopConf
import play.api.libs.ws.ahc.AhcWSClient

import scala.concurrent.duration._
import scala.language.implicitConversions

class Commands(arguments: Seq[String]) extends ScallopConf(arguments) {
  val workers = opt[Int]()
  val master = opt[Int]()
  val num = opt[Int]()
  val developer = opt[Int]()
  val dummy = opt[Int]()
  val seeds = opt[List[String]](default = Some(List()))
  val test = opt[Boolean]()
  verify()
}

object Main extends App {
  case class WorkersConf(numberOfWorkers: Int, seeds: List[String])
  case class MasterConf(port: Int, seeds: List[String], num: Int)
  case class DeveloperConf(port: Int, seeds: List[String])
  case class DummyConf(port: Int, seeds: List[String])

  val cmd = new Commands(args)

  val worker = for {
    workers <- cmd.workers
    seeds <- cmd.seeds
  } yield WorkersConf(workers, seeds)

  val master = for {
    port <- cmd.master
    seeds <- cmd.seeds
    num <- cmd.num
  } yield MasterConf(port, seeds, num)

  val developer = for {
    port <- cmd.developer
    seeds <- cmd.seeds
  } yield DeveloperConf(port, seeds)

  val dummy = for {
    port <- cmd.dummy
    seeds <- cmd.seeds
  } yield DummyConf(port, seeds)

  worker foreach { startWorkers }
  master foreach { startMaster }
  developer foreach { startDeveloper }
  dummy foreach { startDummy }

  def name = "ClusterSystem"

  /**
   * The interval in which a worker register itself to the master
   */
  def registerInterval = 30.seconds

  /**
   * The time after which a worker will be marked as unreachable
   */
  def workerTimeout = 5.minutes

  /**
   * The time after which a work (execution of a generator, filter, etc..) will be cancelled
   */
  def workTimeout = 4.minutes

  /**
   * This time specify how long the session is, to access the database in a docker container (to execute generator, filter, etc..)
   * Note: This time should be longer as the workTimeout, because if workTimeout was reached, the system should be
   * able to store the log of the docker container
   */
  def sessionDuration = workTimeout.plus(2.minutes)

  def startDummy(dummy: DummyConf): Unit = {
    val roles = List("dummy")

    //Thread.sleep(30000)

    val config = ClusterManager.getClusterJoinConfig(roles, dummy.seeds, dummy.port).withFallback(ConfigFactory.load())
    val system = ActorSystem(name, config)

    ClusterSharding(system).startProxy(
      typeName = Mediator.shardRegionName,
      role = Some(Mediator.locatedOnNode),
      extractEntityId = Mediator.extractEntityId,
      extractShardId = Mediator.extractShardId
    )

    system.actorOf(DeveloperDummy.props(), "dummy")

    //val journalAddress = ClusterManager.getJournalPath(dummy.port, dummy.seeds)
    //setSharedJournal(system, journalAddress)
  }

  def startDeveloper(developer: DeveloperConf): Unit = {
    val roles = List(Mediator.locatedOnNode)

    // Before we start we wait to make sure that the other actors started in the system
    Thread.sleep(20000)

    val config = ClusterManager.getClusterJoinConfig(roles, developer.seeds, developer.port).withFallback(ConfigFactory.load())
    val system = ActorSystem(name, config)

    ClusterSharding(system).start(
      typeName = Mediator.shardRegionName,
      entityProps = Props[Mediator],
      settings = ClusterShardingSettings(system),
      extractEntityId = Mediator.extractEntityId,
      extractShardId = Mediator.extractShardId
    )

    system.actorOf(DeveloperManager.props(), "developer")

    val journalAddress = ClusterManager.getJournalPath(developer.port, developer.seeds)
    setSharedJournal(system, journalAddress)
  }

  def startMaster(master: MasterConf): Unit = {
    val roles = List("backend")

    if (master.num == 2) {
      Thread.sleep(10000)
    }

    val config = ClusterManager.getClusterJoinConfig(roles, master.seeds, master.port).withFallback(ConfigFactory.load())
    implicit val system = ActorSystem(name, config)
    implicit val mat = ActorMaterializer()
    implicit val client = AhcWSClient()
    val sessionManager = SyncGatewaySession()

    system.actorOf(
      ClusterSingletonManager.props(
        Master.props(workerTimeout, sessionDuration, sessionManager),
        PoisonPill,
        ClusterSingletonManagerSettings(system).withRole("backend")
      ),
      "master"
    )

    // no seeds? Then this is the first node on which we start the shared level db store
    if (master.seeds.isEmpty) {
      system.actorOf(Props[SharedLeveldbStore], "store")
    }

    val journalAddress = ClusterManager.getJournalPath(master.port, master.seeds)

    println("Journal Address : " + journalAddress.toString)
    setSharedJournal(system, journalAddress)
  }

  def startWorkers(workers: WorkersConf) = {
    Thread.sleep(20000)

    println(s"Start ${workers.numberOfWorkers} workers")
    (1 to workers.numberOfWorkers) foreach (i => startWorker(workers, i, 0))
  }

  def startWorker(workers: WorkersConf, number: Int, port: Int): Unit = {
    val roles = List("worker")
    val config = ClusterManager.getClusterJoinConfig(roles, workers.seeds, port).withFallback(ConfigFactory.load())
    val system = ActorSystem(name, config)

    val executor = system.actorOf(DockerWorkExecutor.props(), s"work-executor-${number}")
    //val executor = system.actorOf(DummyWorkerExecutor.props())

    system.actorOf(Worker.props(executor, registerInterval, workTimeout), s"worker-${number}")
  }

  def setSharedJournal(system: ActorSystem, path: ActorPath): Unit = {
    import system.dispatcher
    implicit val timeout = Timeout(15.seconds)
    val f = (system.actorSelection(path) ? Identify(None))
    f.onSuccess {
      case ActorIdentity(_, Some(ref)) => SharedLeveldbJournal.setStore(ref, system)
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
}
