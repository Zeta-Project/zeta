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
import de.htwg.zeta.common.models.entity.Entity
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Cache
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.CleanUp
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Create
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Delete
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Found
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Read
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Update
import de.htwg.zeta.persistence.general.EntityPersistence

object EntityCacheActor {

  case class Create[E <: Entity](entity: E)

  case class Read(id: UUID)

  case class Update[E <: Entity](id: UUID, updateEntity: E => E)

  case class Delete(id: UUID)

  private case object CleanUp

  private case class Found[E <: Entity](entity: E, inCache: Boolean)

  private case class Cache[E <: Entity](entities: Map[UUID, E], used: Set[UUID])

  def props[E <: Entity](underlying: EntityPersistence[E], cacheDuration: FiniteDuration): Props = Props(new EntityCacheActor(underlying, cacheDuration))

}

class EntityCacheActor[E <: Entity](underlying: EntityPersistence[E], cacheDuration: FiniteDuration) extends Actor {

  private val cleanUpJob: Cancellable = context.system.scheduler.schedule(cacheDuration, cacheDuration, self, CleanUp)

  override def receive: Receive = state(Future.successful(Cache(Map.empty, Set.empty)))

  private type F = E => E

  private def state(cache: Future[Cache[E]]): Receive = {
    case Create(entity: E) => context.become(state(create(cache, entity, sender)))
    case Read(id) => context.become(state(read(cache, id, sender)))
    case Update(id, updateEntity: F) => context.become(state(update(cache, id, updateEntity, sender)))
    case Delete(id) => context.become(state(delete(cache, id, sender)))
    case CleanUp => context.become(state(cleanUp(cache)))
  }

  private def create(cache: Future[Cache[E]], entity: E, sender: ActorRef): Future[Cache[E]] = {
    cache.flatMap { cache =>
      underlying.create(entity).map { entity =>
        sender ! Success(entity)
        cache.copy(
          entities = cache.entities + (entity.id -> entity),
          used = cache.used + entity.id
        )
      }.recover { case e: Exception =>
        sender ! Failure(e)
        cache
      }
    }
  }

  private def read(cache: Future[Cache[E]], id: UUID, sender: ActorRef): Future[Cache[E]] = {
    cache.flatMap { cache =>
      cache.entities.get(id).fold[Future[Found[E]]] {
        underlying.read(id).map(Found(_, inCache = false))
      } { entity =>
        Future(Found(entity, inCache = true))
      }.map { found =>
        sender ! Success(found.entity)
        if (found.inCache) {
          cache.copy(used = cache.used + id)
        } else {
          cache.copy(
            entities = cache.entities + (id -> found.entity),
            used = cache.used + id
          )
        }
      }.recover { case e: Exception =>
        sender ! Failure(e)
        cache
      }
    }
  }

  private def update(cache: Future[Cache[E]], id: UUID, updateEntity: E => E, sender: ActorRef): Future[Cache[E]] = {
    cache.flatMap { cache =>
      underlying.update(id, updateEntity).map { entity =>
        sender ! Success(entity)
        cache.copy(
          entities = cache.entities + (entity.id -> entity),
          used = cache.used + entity.id
        )
      }.recover { case e: Exception =>
        sender ! Failure(e)
        cache
      }
    }
  }

  private def delete(cache: Future[Cache[E]], id: UUID, sender: ActorRef): Future[Cache[E]] = {
    cache.flatMap { cache =>
      underlying.delete(id).map { _ =>
        sender ! Success(Unit)
        cache.copy(
          entities = cache.entities - id,
          used = cache.used - id
        )
      }.recover { case e: Exception =>
        sender ! Failure(e)
        cache
      }
    }
  }

  private def cleanUp(cache: Future[Cache[E]]): Future[Cache[E]] = {
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
