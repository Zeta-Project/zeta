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
import de.htwg.zeta.persistence.actorCache.LoginInfoCacheActor.CleanUp
import de.htwg.zeta.persistence.actorCache.LoginInfoCacheActor.Create
import de.htwg.zeta.persistence.actorCache.LoginInfoCacheActor.Delete
import de.htwg.zeta.persistence.actorCache.LoginInfoCacheActor.Read
import de.htwg.zeta.persistence.actorCache.LoginInfoCacheActor.Update
import de.htwg.zeta.persistence.actorCache.LoginInfoCacheActor.unitFuture
import de.htwg.zeta.persistence.authInfo.ZetaLoginInfo
import de.htwg.zeta.persistence.general.LoginInfoRepository
import grizzled.slf4j.Logging


private[actorCache] object LoginInfoCacheActor {

  case class Create(loginInfo: ZetaLoginInfo, userId: UUID)

  case class Read(loginInfo: ZetaLoginInfo)

  case class Update(old: ZetaLoginInfo, updated: ZetaLoginInfo)

  case class Delete(loginInfo: ZetaLoginInfo)

  private case object CleanUp

  private val unitFuture: Future[Unit] = Future.successful(())

  def props(underlying: LoginInfoRepository, cacheDuration: FiniteDuration): Props = Props(new LoginInfoCacheActor(underlying, cacheDuration))

}

private[actorCache] class LoginInfoCacheActor(underlying: LoginInfoRepository, cacheDuration: FiniteDuration) extends Actor with Logging {

  private val cache: mutable.Map[ZetaLoginInfo, Future[UUID]] = mutable.Map.empty

  private val used: mutable.Set[ZetaLoginInfo] = mutable.Set.empty

  private val cleanUpJob: Cancellable = context.system.scheduler.schedule(cacheDuration, cacheDuration, self, CleanUp)

  override def receive: Receive = {
    case Create(loginInfo, userId) => create(loginInfo, userId)
    case Read(loginInfo) => read(loginInfo)
    case Update(old, updated) => update(old, updated)
    case Delete(loginInfo) => delete(loginInfo)
    case CleanUp => cleanUp()
  }

  private def create(loginInfo: ZetaLoginInfo, userId: UUID): Unit = {
    val entry = mapOrRecoverToUnit(cache.get(loginInfo)).flatMap(_ => underlying.create(loginInfo, userId))
    replyToSender(entry, sender)
    cache += (loginInfo -> entry.map(_ => userId))
    used += loginInfo
  }

  private def read(loginInfo: ZetaLoginInfo): Unit = {
    val entry = cache.get(loginInfo).fold(underlying.read(loginInfo))(_.recoverWith { case _ => underlying.read(loginInfo) })
    replyToSender(entry, sender)
    cache += (loginInfo -> entry)
    used += loginInfo
  }

  private def update(old: ZetaLoginInfo, updated: ZetaLoginInfo): Unit = {
    val entry = mapOrRecoverToUnit(cache.get(old)).flatMap(_ => underlying.update(old, updated))
    replyToSender(entry, sender)
    cache -= old
    used -= old
  }

  private def delete(loginInfo: ZetaLoginInfo): Unit = {
    val entry = mapOrRecoverToUnit(cache.get(loginInfo)).flatMap(_ => underlying.delete(loginInfo))
    replyToSender(entry, sender)
    cache -= loginInfo
    used -= loginInfo
  }

  private def mapOrRecoverToUnit(f: Option[Future[UUID]]): Future[Unit] = {
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
