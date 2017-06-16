package de.htwg.zeta.generatorControl.start

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

import akka.actor.ActorSystem
import akka.actor.PoisonPill
import akka.actor.Props
import akka.cluster.singleton.ClusterSingletonManager
import akka.cluster.singleton.ClusterSingletonManagerSettings
import akka.persistence.journal.leveldb.SharedLeveldbStore
import akka.stream.ActorMaterializer
import de.htwg.zeta.common.cluster.ClusterManager
import de.htwg.zeta.generatorControl.actors.master.Master
import org.rogach.scallop.ScallopOption
import org.slf4j.LoggerFactory
import play.api.libs.ws.ahc.AhcWSClient


/**
 */
class MasterStarter(config: MasterConfig) extends Starter {


  private val logger = LoggerFactory.getLogger(MasterStarter.getClass)

  def start(): Unit = {
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

  private def createActor(config: MasterConfig) = {
    implicit val system = createActorSystem(MasterStarter.ActorRole, config.seeds, config.port)
    implicit val mat = ActorMaterializer()
    implicit val client = AhcWSClient()

    system.actorOf(
      ClusterSingletonManager.props(
        Master.props(MasterStarter.WorkerTimeout, MasterStarter.SessionDuration),
        PoisonPill,
        ClusterSingletonManagerSettings(system).withRole(MasterStarter.ActorRole)
      ),
      MasterStarter.ActorName
    )

    system
  }

  private def startSharedLevelDbOnFirstMaster(config: MasterConfig, system: ActorSystem) = {
    if (config.seeds.isEmpty) {
      system.actorOf(Props[SharedLeveldbStore], MasterStarter.ActorSharedLevelDbStore)
    }
  }
}

object MasterStarter {
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

  def apply(cmd: Commands): Option[MasterStarter] = {
    val config: ScallopOption[MasterConfig] = for {
      port <- cmd.masterPort
      seeds <- cmd.masterSeeds
      num <- cmd.masterNum
    } yield {
      MasterConfig(port, seeds, num)
    }
    config.toOption.map(new MasterStarter(_))
  }
}

case class MasterConfig(port: Int, seeds: List[String], num: Int)
