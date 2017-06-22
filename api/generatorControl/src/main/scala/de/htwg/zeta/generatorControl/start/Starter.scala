package de.htwg.zeta.generatorControl.start

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.implicitConversions

import akka.actor.ActorIdentity
import akka.actor.ActorPath
import akka.actor.ActorSystem
import akka.actor.Identify
import akka.pattern.ask
import akka.persistence.journal.leveldb.SharedLeveldbJournal
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import de.htwg.zeta.common.cluster.ClusterManager

/**
 */
protected trait Starter {
  def start(): Unit

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
    val config = ClusterManager.getClusterJoinConfig(roles, seeds, port).withFallback(ConfigFactory.load()).resolve()
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
