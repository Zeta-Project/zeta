package de.htwg.zeta.generatorControl.start

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.implicitConversions
import scala.util.Failure
import scala.util.Success

import akka.actor.ActorIdentity
import akka.actor.ActorPath
import akka.actor.ActorSystem
import akka.actor.Identify
import akka.pattern.ask
import akka.persistence.journal.leveldb.SharedLeveldbJournal
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.typesafe.config.impl.ConfigImpl
import de.htwg.zeta.common.cluster.ClusterManager
import grizzled.slf4j.Logging

/**
 */
protected trait Starter extends Logging {
  def start(): Unit

  protected def setSharedJournal(system: ActorSystem, path: ActorPath): Unit = {
    implicit val timeout: Timeout = Timeout(Duration(15, TimeUnit.SECONDS))
    val f = system.actorSelection(path) ? Identify(None)
    f.onComplete {
      case Success(ActorIdentity(_, Some(ref))) =>
          SharedLeveldbJournal.setStore(ref, system)
      case Failure(t) =>
        system.log.error("Lookup of shared journal at {} timed out", path)
        system.terminate()
    }
  }

  protected def createActorSystem(role: String, seeds: List[String], port: Int): ActorSystem = {
    val roles = List(role)
    val clusterConfig = ClusterManager.getClusterJoinConfig(roles, seeds, port).withFallback(ConfigFactory.load())
    val config = ConfigImpl.systemPropertiesAsConfig().withFallback(clusterConfig).resolve()
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
