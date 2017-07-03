package de.htwg.zeta.generatorControl.start

import scala.language.implicitConversions

import akka.actor.Props
import akka.cluster.client.ClusterClientReceptionist
import akka.cluster.sharding.ClusterSharding
import akka.cluster.sharding.ClusterShardingSettings
import de.htwg.zeta.common.cluster.ClusterManager
import de.htwg.zeta.generatorControl.actors.developer.Mediator
import de.htwg.zeta.generatorControl.actors.frontend.DeveloperFrontend
import de.htwg.zeta.generatorControl.actors.frontend.GeneratorFrontend
import de.htwg.zeta.generatorControl.actors.frontend.UserFrontend
import de.htwg.zeta.generatorControl.actors.frontendManager.FrontendManager

/**
 */
class DeveloperStarter(developer: DeveloperConfig) extends Starter {


  def start(): Unit = {
    debug(DeveloperStarter.LogStart.format(developer.toString))
    // Thread.sleep(DeveloperStarter.MilliSecWaitForOtherActors)  // FIXME is the sleep necessary
    val system = createActorSystem(Mediator.locatedOnNode, developer.seeds, developer.port)

    ClusterSharding(system).start(
      typeName = Mediator.shardRegionName,
      entityProps = Props[Mediator],
      settings = ClusterShardingSettings(system),
      extractEntityId = Mediator.extractEntityId,
      extractShardId = Mediator.extractShardId
    )

    val receptionist: ClusterClientReceptionist = ClusterClientReceptionist(system)
    receptionist.registerService(system.actorOf(FrontendManager.props(DeveloperFrontend), DeveloperFrontend.developerFrontendService))
    receptionist.registerService(system.actorOf(FrontendManager.props(GeneratorFrontend), GeneratorFrontend.generatorFrontendService))
    receptionist.registerService(system.actorOf(FrontendManager.props(UserFrontend), UserFrontend.userFrontendService))


    val journalAddress = ClusterManager.getJournalPath(developer.port, developer.seeds)
    setSharedJournal(system, journalAddress)
  }
}

object DeveloperStarter {
  val ActorName = "developer"
  val LogStart = "Start developer actor: %s"
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
