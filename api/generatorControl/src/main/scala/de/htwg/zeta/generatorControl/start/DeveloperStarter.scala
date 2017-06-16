package de.htwg.zeta.generatorControl.start

import scala.language.implicitConversions

import akka.actor.Props
import akka.cluster.sharding.ClusterSharding
import akka.cluster.sharding.ClusterShardingSettings
import de.htwg.zeta.common.cluster.ClusterManager
import de.htwg.zeta.generatorControl.actors.developer.DeveloperManager
import de.htwg.zeta.generatorControl.actors.developer.Mediator
import org.slf4j.LoggerFactory

/**
 */
class DeveloperStarter(developer: DeveloperConfig) extends Starter {


  private val logger = LoggerFactory.getLogger(DeveloperStarter.getClass)

  def start(): Unit = {
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

object DeveloperStarter {
  val ActorName = "developer"
  val LogStart = "Start developer actor: {}"
  val MilliSecWaitForOtherActors = 20000

  def apply(cmd: Commands): Option[DeveloperStarter] = {
    val developer = for {
      port <- cmd.devPort
      seeds <- cmd.devSeeds
    } yield {
      DeveloperConfig(port, seeds)
    }

    developer.toOption.map(new DeveloperStarter(_))
  }
}

case class DeveloperConfig(port: Int, seeds: List[String])
