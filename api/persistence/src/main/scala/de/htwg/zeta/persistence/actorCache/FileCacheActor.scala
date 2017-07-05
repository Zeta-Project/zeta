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
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.persistence.actorCache.FileCacheActor.CleanUp
import de.htwg.zeta.persistence.actorCache.FileCacheActor.Create
import de.htwg.zeta.persistence.actorCache.FileCacheActor.Delete
import de.htwg.zeta.persistence.actorCache.FileCacheActor.Read
import de.htwg.zeta.persistence.actorCache.FileCacheActor.Update
import de.htwg.zeta.persistence.actorCache.FileCacheActor.unitFuture
import de.htwg.zeta.persistence.general.FilePersistence
import grizzled.slf4j.Logging

private[actorCache] object FileCacheActor {

  case class Create(file: File)

  case class Read(id: UUID, name: String)

  case class Update(file: File)

  case class Delete(id: UUID, name: String)

  private case object CleanUp

  private val unitFuture: Future[Unit] = Future.successful(())

  def props(underlying: FilePersistence, cacheDuration: FiniteDuration): Props = Props(new FileCacheActor(underlying, cacheDuration))

}

private[actorCache] class FileCacheActor(underlying: FilePersistence, cacheDuration: FiniteDuration) extends Actor with Logging {

  private val cache: mutable.Map[(UUID, String), Future[File]] = mutable.Map.empty

  private val used: mutable.Set[(UUID, String)] = mutable.Set.empty

  private val cleanUpJob: Cancellable = context.system.scheduler.schedule(cacheDuration, cacheDuration, self, CleanUp)

  override def receive: Receive = {
    case Create(file: File) => create(file)
    case Read(id, name) => read(id, name)
    case Update(file: File) => update(file)
    case Delete(id, name) => delete(id, name)
    case CleanUp => cleanUp()
  }

  private def create(file: File): Unit = {
    val entry = mapOrRecoverToUnit(cache.get(file.key)).flatMap(_ => underlying.create(file))
    replyToSender(entry, sender)
    cache += (file.key -> entry)
    used += file.key
  }

  private def read(id: UUID, name: String): Unit = {
    val key = (id, name)
    val entry = cache.get(key).fold(underlying.read(id, name))(_.recoverWith { case _ => underlying.read(id, name) })
    replyToSender(entry, sender)
    cache += (key -> entry)
    used += key
  }

  private def update(file: File): Unit = {
    val entry = mapOrRecoverToUnit(cache.get(file.key)).flatMap(_ => underlying.update(file))
    replyToSender(entry, sender)
    cache += (file.key -> entry)
    used += file.key
  }

  private def delete(id: UUID, name: String): Unit = {
    val key = (id, name)
    val entry = mapOrRecoverToUnit(cache.get(key)).flatMap(_ => underlying.delete(id, name))
    replyToSender(entry, sender)
    cache -= key
    used -= key
  }

  private def mapOrRecoverToUnit(f: Option[Future[File]]): Future[Unit] = {
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
