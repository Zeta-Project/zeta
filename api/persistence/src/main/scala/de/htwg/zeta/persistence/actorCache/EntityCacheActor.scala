package de.htwg.zeta.persistence.actorCache

import java.util.UUID

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.reflect.ClassTag
import scala.util.Failure
import scala.util.Success

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Cancellable
import akka.actor.Props
import de.htwg.zeta.common.models.entity.Entity
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.CleanUp
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Create
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Delete
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Read
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Update
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.success
import de.htwg.zeta.persistence.general.EntityPersistence
import grizzled.slf4j.Logging

private[actorCache] object EntityCacheActor {

  case class Create[E <: Entity](entity: E)

  case class Read(id: UUID)

  case class Update[E <: Entity](id: UUID, updateEntity: E => E)

  case class Delete(id: UUID)

  private case object CleanUp

  private val success: Future[Unit] = Future.successful(())

  def props[E <: Entity](underlying: EntityPersistence[E], cacheDuration: FiniteDuration): Props = Props(new EntityCacheActor(underlying, cacheDuration))

}

private[actorCache] class EntityCacheActor[E <: Entity](underlying: EntityPersistence[E], cacheDuration: FiniteDuration) extends Actor with Logging {

  private val cache: mutable.Map[UUID, Future[E]] = mutable.Map.empty

  private val used: mutable.Set[UUID] = mutable.Set.empty

  private val cleanUpJob: Cancellable = context.system.scheduler.schedule(cacheDuration, cacheDuration, self, CleanUp)

  private type F = E => E

  override def receive: Receive = {
    case Create(entity: E) => create(entity, sender)
    case Read(id) => read(id, sender)
    case Update(id, updateEntity: F) => update(id, updateEntity, sender)
    case Delete(id) => delete(id, sender)
    case CleanUp => cleanUp()
  }

  private def create(entity: E, sender: ActorRef): Unit = {
    trace("creating - " + entity.id.toString)

    val current = cache.getOrElse(entity.id, success).recoverWith { case _ => success }.flatMap(_ => underlying.create(entity))

    current.onComplete {
      case Success(_) => sender ! Success(entity)
      case Failure(e) => sender ! Failure(e)
    }

    cache += (entity.id -> current)
    used += entity.id
  }

  private def read(id: UUID, sender: ActorRef): Unit = {
    trace("reading - " + id.toString)

    val current = cache.get(id).fold(underlying.read(id))(_.recoverWith { case _ => underlying.read(id) })

    current.onComplete {
      case Success(entity) => sender ! Success(entity)
      case Failure(e) => sender ! Failure(e)
    }

    cache.update(id, current)
    used.add(id)
  }

  private def update(id: UUID, updateEntity: E => E, sender: ActorRef): Unit = {
    trace("updating - " + id.toString)

    val current = cache.getOrElse(id, success).recoverWith { case _ => success }.flatMap(_ => underlying.update(id, updateEntity))

    current.onComplete {
      case Success(entity) => sender ! Success(entity)
      case Failure(e) => sender ! Failure(e)
    }

    cache.update(id, current)
    used.add(id)
  }

  private def delete(id: UUID, sender: ActorRef): Unit = {
    trace("deleting - " + id.toString)
    val current = cache.getOrElse(id, success).recoverWith { case _ => success }.flatMap(_ => underlying.delete(id))

    current.onComplete {
      case Success(_) => sender ! Success(Unit)
      case Failure(e) => sender ! Failure(e)
    }

    cache.remove(id)
    used.remove(id)
  }

  private def cleanUp(): Unit = {
    trace("cleaning cache")

    val unused = cache.keySet.filter(!used.contains(_))
    unused.foreach(cache.remove)
    used.clear()
  }

  override def postStop(): Unit = {
    cleanUpJob.cancel()
  }

}
