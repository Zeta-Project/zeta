package de.htwg.zeta.generatorControl.start

import scala.language.implicitConversions

import akka.cluster.sharding.ClusterSharding
import de.htwg.zeta.generatorControl.actors.developer.Mediator
import de.htwg.zeta.generatorControl.actors.frontend.DeveloperDummy
import org.slf4j.LoggerFactory

/**
 */
class DummyStarter(conf: DummyConfig) extends Starter {


  private val logger = LoggerFactory.getLogger(DummyStarter.getClass)


  def start(): Unit = {
    logger.debug(DummyStarter.LogStart, conf.toString)
    val system = createActorSystem(DummyStarter.ActorRole, conf.seeds, conf.port)

    ClusterSharding(system).startProxy(
      typeName = Mediator.shardRegionName,
      role = Some(Mediator.locatedOnNode),
      extractEntityId = Mediator.extractEntityId,
      extractShardId = Mediator.extractShardId
    )

    system.actorOf(DeveloperDummy.props(), DummyStarter.ActorName)
  }
}

object DummyStarter {
  val ActorName = "dummy"
  val ActorRole = "dummy"
  val LogStart = "Start dummy actor: {}"

  def apply(cmd: Commands): Option[DummyStarter] = {
    val config = for {
      port <- cmd.dummyPort
      seeds <- cmd.dummySeeds
    } yield {
      DummyConfig(port, seeds)
    }
    config.toOption.map(new DummyStarter(_))
  }

}

case class DummyConfig(port: Int, seeds: List[String])
