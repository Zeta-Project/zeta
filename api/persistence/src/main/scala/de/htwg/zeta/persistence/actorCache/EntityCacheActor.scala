package de.htwg.zeta.persistence.actorCache

import java.util.UUID

import scala.collection.mutable
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
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.CleanUp
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Create
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Delete
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Read
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Update
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.unitFuture
import de.htwg.zeta.persistence.general.EntityRepository

private[actorCache] object EntityCacheActor {

  case class Create[E <: Entity](entity: E)

  case class Read(id: UUID)

  case class Update[E <: Entity](id: UUID, updateEntity: E => E)

  case class Delete(id: UUID)

  private case object CleanUp

  private val unitFuture: Future[Unit] = Future.successful(())

  def props[E <: Entity](underlying: EntityRepository[E], cacheDuration: FiniteDuration): Props = Props(new EntityCacheActor(underlying, cacheDuration))

}

private[actorCache] class EntityCacheActor[E <: Entity](underlying: EntityRepository[E], cacheDuration: FiniteDuration) extends Actor {

  private val cache: mutable.Map[UUID, Future[E]] = mutable.Map.empty

  private val used: mutable.Set[UUID] = mutable.Set.empty

  private val cleanUpJob: Cancellable = context.system.scheduler.scheduleAtFixedRate(cacheDuration, cacheDuration, self, CleanUp)

  private type F = E => E

  override def receive: Receive = {
    case Create(entity: E) => create(entity)
    case Read(id) => read(id)
    case Update(id, updateEntity: F) => update(id, updateEntity)
    case Delete(id) => delete(id)
    case CleanUp => cleanUp()
  }

  private def create(entity: E): Unit = {
    val entry = mapOrRecoverToUnit(cache.get(entity.id)).flatMap(_ => underlying.create(entity))
    replyToSender(entry, sender)
    cache += (entity.id -> entry)
    used += entity.id
  }

  private def read(id: UUID): Unit = {
    val entry = cache.get(id).fold(underlying.read(id))(_.recoverWith { case _ => underlying.read(id) })
    replyToSender(entry, sender)
    cache += (id -> entry)
    used += id
  }

  private def update(id: UUID, updateEntity: E => E): Unit = {
    val entry = mapOrRecoverToUnit(cache.get(id)).flatMap(_ => underlying.update(id, updateEntity))
    replyToSender(entry, sender)
    cache += (id -> entry)
    used += id
  }

  private def delete(id: UUID): Unit = {
    val entry = mapOrRecoverToUnit(cache.get(id)).flatMap(_ => underlying.delete(id))
    replyToSender(entry, sender)
    cache -= id
    used -= id
  }

  private def mapOrRecoverToUnit(f: Option[Future[E]]): Future[Unit] = {
    f.fold(unitFuture)(_.flatMap(_ => unitFuture).recoverWith { case _ => unitFuture })
  }

  private def replyToSender[T](f: Future[T], target: ActorRef): Unit = {
    f.onComplete {
      case Success(s) => target ! Success(s)
      case Failure(e) => target ! Failure(e)
    }
  }

  private def cleanUp(): Unit = {
    val unused = cache.keySet.filter(!used.contains(_))
    unused.foreach(cache.remove)
    used.clear()
  }

  override def postStop(): Unit = {
    cleanUpJob.cancel()
  }

}
