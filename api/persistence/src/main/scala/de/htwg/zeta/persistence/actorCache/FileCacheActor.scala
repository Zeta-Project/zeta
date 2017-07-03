package de.htwg.zeta.persistence.actorCache

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.util.Failure
import scala.util.Success

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Cancellable
import akka.actor.Props
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.persistence.actorCache.FileCacheActor.Cache
import de.htwg.zeta.persistence.actorCache.FileCacheActor.CleanUp
import de.htwg.zeta.persistence.actorCache.FileCacheActor.Create
import de.htwg.zeta.persistence.actorCache.FileCacheActor.Delete
import de.htwg.zeta.persistence.actorCache.FileCacheActor.Found
import de.htwg.zeta.persistence.actorCache.FileCacheActor.Read
import de.htwg.zeta.persistence.actorCache.FileCacheActor.Update
import de.htwg.zeta.persistence.general.FilePersistence
import grizzled.slf4j.Logging

private[actorCache] object FileCacheActor {

  case class Create(file: File)

  case class Read(id: UUID, name: String)

  case class Update(file: File)

  case class Delete(id: UUID, name: String)

  private case object CleanUp

  private case class Found(file: File, inCache: Boolean)

  private case class Cache(entities: Map[(UUID, String), File], used: Set[(UUID, String)])

  def props(underlying: FilePersistence, cacheDuration: FiniteDuration): Props = Props(new FileCacheActor(underlying, cacheDuration))

}

private[actorCache] class FileCacheActor(underlying: FilePersistence, cacheDuration: FiniteDuration) extends Actor with Logging {

  private val cleanUpJob: Cancellable = context.system.scheduler.schedule(cacheDuration, cacheDuration, self, CleanUp)

  override def receive: Receive = state(Future.successful(Cache(Map.empty, Set.empty)))

  private def state(cache: Future[Cache]): Receive = {
    case Create(file: File) => context.become(state(create(cache, file, sender)))
    case Read(id, name) => context.become(state(read(cache, id, name, sender)))
    case Update(file: File) => context.become(state(update(cache, file, sender)))
    case Delete(id, name) => context.become(state(delete(cache, id, name, sender)))
    case CleanUp => context.become(state(cleanUp(cache)))
  }

  private def create(cache: Future[Cache], file: File, sender: ActorRef): Future[Cache] = {
    trace(s"creating - ${file.id.toString} - ${file.name}") // scalastyle:ignore multiple.string.literals
    cache.flatMap { cache =>
      underlying.create(file).map { file =>
        sender ! Success(file)
        cache.copy(
          entities = cache.entities + ((file.id -> file.name) -> file),
          used = cache.used + (file.id -> file.name)
        )
      }.recover { case e: Exception =>
        sender ! Failure(e)
        cache
      }
    }
  }

  private def read(cache: Future[Cache], id: UUID, name: String, sender: ActorRef): Future[Cache] = {
    trace(s"reading - ${id.toString} - $name")
    cache.flatMap { cache =>
      cache.entities.get(id -> name).fold[Future[Found]] {
        underlying.read(id, name).map(Found(_, inCache = false))
      } { file =>
        Future(Found(file, inCache = true))
      }.map { found =>
        sender ! Success(found.file)
        if (found.inCache) {
          cache.copy(used = cache.used + (id -> name))
        } else {
          cache.copy(
            entities = cache.entities + ((id -> name) -> found.file),
            used = cache.used + (id -> name)
          )
        }
      }.recover { case e: Exception =>
        sender ! Failure(e)
        cache
      }
    }
  }

  private def update(cache: Future[Cache], file: File, sender: ActorRef): Future[Cache] = {
    trace(s"updating - ${file.id.toString} - ${file.name}")
    cache.flatMap { cache =>
      underlying.update(file).map { file =>
        sender ! Success(file)
        cache.copy(
          entities = cache.entities + ((file.id -> file.name) -> file),
          used = cache.used + (file.id -> file.name)
        )
      }.recover { case e: Exception =>
        sender ! Failure(e)
        cache
      }
    }
  }

  private def delete(cache: Future[Cache], id: UUID, name: String, sender: ActorRef): Future[Cache] = {
    trace(s"deleting - ${id.toString} - $name")
    cache.flatMap { cache =>
      underlying.delete(id, name).map { _ =>
        sender ! Success(Unit)
        cache.copy(
          entities = cache.entities - (id -> name),
          used = cache.used - (id -> name)
        )
      }.recover { case e: Exception =>
        sender ! Failure(e)
        cache
      }
    }
  }

  private def cleanUp(cache: Future[Cache]): Future[Cache] = {
    trace("cleaning cache")
    cache.map { cache =>
      cache.copy(
        cache.used.map(id => (id, cache.entities(id))).toMap,
        Set.empty
      )
    }
  }

  override def postStop(): Unit = {
    cleanUpJob.cancel()
  }

}
