package de.htwg.zeta.persistence.actorCache

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
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Add
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.CleanUp
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Find
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Remove
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Save
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Update
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.unitFuture
import de.htwg.zeta.persistence.authInfo.ZetaLoginInfo
import de.htwg.zeta.persistence.authInfo.ZetaPasswordInfo
import de.htwg.zeta.persistence.general.PasswordInfoRepository
import grizzled.slf4j.Logging


private[actorCache] object PasswordInfoCacheActor {

  case class Add(loginInfo: ZetaLoginInfo, authInfo: ZetaPasswordInfo)

  case class Find(loginInfo: ZetaLoginInfo)

  case class Update(loginInfo: ZetaLoginInfo, authInfo: ZetaPasswordInfo)

  case class Save(loginInfo: ZetaLoginInfo, authInfo: ZetaPasswordInfo)

  case class Remove(loginInfo: ZetaLoginInfo)

  private case object CleanUp

  private val unitFuture: Future[Unit] = Future.successful(())

  def props(underlying: PasswordInfoRepository, cacheDuration: FiniteDuration): Props = Props(new PasswordInfoCacheActor(underlying, cacheDuration))

}

private[actorCache] class PasswordInfoCacheActor(underlying: PasswordInfoRepository, cacheDuration: FiniteDuration) extends Actor with Logging {

  private val cache: mutable.Map[ZetaLoginInfo, Future[Option[ZetaPasswordInfo]]] = mutable.Map.empty

  private val used: mutable.Set[ZetaLoginInfo] = mutable.Set.empty

  private val cleanUpJob: Cancellable = context.system.scheduler.scheduleAtFixedRate(cacheDuration, cacheDuration, self, CleanUp)

  override def receive: Receive = {
    case Add(loginInfo, authInfo) => add(loginInfo, authInfo)
    case Find(loginInfo) => find(loginInfo)
    case Update(loginInfo, authInfo) => update(loginInfo, authInfo)
    case Save(loginInfo, authInfo) => save(loginInfo, authInfo)
    case Remove(loginInfo) => remove(loginInfo)
    case CleanUp => cleanUp()
  }

  private def add(loginInfo: ZetaLoginInfo, authInfo: ZetaPasswordInfo): Unit = {
    val entry = mapOrRecoverToUnit(cache.get(loginInfo)).flatMap(_ => underlying.add(loginInfo, authInfo))
    replyToSender(entry, sender)
    cache += (loginInfo -> entry.map(Some(_)))
    used += loginInfo
  }

  private def find(loginInfo: ZetaLoginInfo): Unit = {
    val entry = cache.get(loginInfo).fold(underlying.find(loginInfo))(_.recoverWith { case _ => underlying.find(loginInfo) })
    replyToSender(entry, sender)
    cache += (loginInfo -> entry)
    used += loginInfo
  }

  private def update(loginInfo: ZetaLoginInfo, authInfo: ZetaPasswordInfo): Unit = {
    val entry = mapOrRecoverToUnit(cache.get(loginInfo)).flatMap(_ => underlying.update(loginInfo, authInfo))
    replyToSender(entry, sender)
    cache += (loginInfo -> entry.map(Option(_)))
    used += loginInfo
  }

  private def save(loginInfo: ZetaLoginInfo, authInfo: ZetaPasswordInfo): Unit = {
    val entry = mapOrRecoverToUnit(cache.get(loginInfo)).flatMap(_ => underlying.save(loginInfo, authInfo))
    replyToSender(entry, sender)
    cache += (loginInfo -> entry.map(Option(_)))
    used += loginInfo
  }

  private def remove(loginInfo: ZetaLoginInfo): Unit = {
    val entry = mapOrRecoverToUnit(cache.get(loginInfo)).flatMap(_ => underlying.remove(loginInfo))
    replyToSender(entry, sender)
    cache -= loginInfo
    used -= loginInfo
  }

  private def mapOrRecoverToUnit(f: Option[Future[Option[ZetaPasswordInfo]]]): Future[Unit] = {
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
