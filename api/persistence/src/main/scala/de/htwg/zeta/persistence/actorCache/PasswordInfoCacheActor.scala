package de.htwg.zeta.persistence.actorCache

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.util.Failure
import scala.util.Success

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Cancellable
import akka.actor.Props
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Add
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Cache
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.CleanUp
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Find
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Found
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Remove
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Save
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Update
import de.htwg.zeta.persistence.general.PasswordInfoPersistence
import grizzled.slf4j.Logging


private[actorCache] object PasswordInfoCacheActor {

  case class Add(loginInfo: LoginInfo, authInfo: PasswordInfo)

  case class Find(loginInfo: LoginInfo)

  case class Update(loginInfo: LoginInfo, authInfo: PasswordInfo)

  case class Save(loginInfo: LoginInfo, authInfo: PasswordInfo)

  case class Remove(loginInfo: LoginInfo)

  private case object CleanUp

  private case class Found(authInfo: Option[PasswordInfo], inCache: Boolean)

  private case class Cache(entities: Map[LoginInfo, Option[PasswordInfo]], used: Set[LoginInfo])

  def props(underlying: PasswordInfoPersistence, cacheDuration: FiniteDuration): Props = Props(new PasswordInfoCacheActor(underlying, cacheDuration))

}

private[actorCache] class PasswordInfoCacheActor(underlying: PasswordInfoPersistence, cacheDuration: FiniteDuration) extends Actor with Logging {

  private val cleanUpJob: Cancellable = context.system.scheduler.schedule(cacheDuration, cacheDuration, self, CleanUp)

  override def receive: Receive = state(Future.successful(Cache(Map.empty, Set.empty)))

  private def state(cache: Future[Cache]): Receive = {
    case Add(loginInfo, authInfo) => context.become(state(add(cache, loginInfo, authInfo, sender)))
    case Find(loginInfo) => context.become(state(find(cache, loginInfo, sender)))
    case Update(loginInfo, authInfo) => context.become(state(update(cache, loginInfo, authInfo, sender)))
    case Save(loginInfo, authInfo) => context.become(state(save(cache, loginInfo, authInfo, sender)))
    case Remove(loginInfo) => context.become(state(remove(cache, loginInfo, sender)))
    case CleanUp => context.become(state(cleanUp(cache)))
  }

  private def add(cache: Future[Cache], loginInfo: LoginInfo, authInfo: PasswordInfo, sender: ActorRef): Future[Cache] = {
    trace(s"adding - ${loginInfo.toString}") // scalastyle:ignore multiple.string.literals
    cache.flatMap { cache =>
      underlying.add(loginInfo, authInfo).map { authInfo =>
        sender ! Success(authInfo)
        cache.copy(
          entities = cache.entities + (loginInfo -> Some(authInfo)),
          used = cache.used + loginInfo
        )
      }.recover { case e: Exception =>
        sender ! Failure(e)
        cache
      }
    }
  }

  private def find(cache: Future[Cache], loginInfo: LoginInfo, sender: ActorRef): Future[Cache] = {
    trace(s"finding - ${loginInfo.toString}")
    cache.flatMap { cache =>
      cache.entities.get(loginInfo).fold[Future[Found]] {
        underlying.find(loginInfo).map(Found(_, inCache = false))
      } { authInfo =>
        Future(Found(authInfo, inCache = true))
      }.map { found =>
        sender ! Success(found.authInfo)
        if (found.inCache) {
          cache.copy(used = cache.used + loginInfo)
        } else {
          cache.copy(
            entities = cache.entities + (loginInfo -> found.authInfo),
            used = cache.used + loginInfo
          )
        }
      }.recover { case e: Exception =>
        sender ! Failure(e)
        cache
      }
    }
  }

  private def update(cache: Future[Cache], loginInfo: LoginInfo, authInfo: PasswordInfo, sender: ActorRef): Future[Cache] = {
    trace(s"updating - ${loginInfo.toString}")
    cache.flatMap { cache =>
      underlying.update(loginInfo, authInfo).map { authInfo =>
        sender ! Success(authInfo)
        cache.copy(
          entities = cache.entities + (loginInfo -> Some(authInfo)),
          used = cache.used + loginInfo
        )
      }.recover { case e: Exception =>
        sender ! Failure(e)
        cache
      }
    }
  }

  private def save(cache: Future[Cache], loginInfo: LoginInfo, authInfo: PasswordInfo, sender: ActorRef): Future[Cache] = {
    trace(s"updating - ${loginInfo.toString}")
    cache.flatMap { cache =>
      underlying.save(loginInfo, authInfo).map { authInfo =>
        sender ! Success(authInfo)
        cache.copy(
          entities = cache.entities + (loginInfo -> Some(authInfo)),
          used = cache.used + loginInfo
        )
      }.recover { case e: Exception =>
        sender ! Failure(e)
        cache
      }
    }
  }

  private def remove(cache: Future[Cache], loginInfo: LoginInfo, sender: ActorRef): Future[Cache] = {
    trace(s"deleting - ${loginInfo.toString}")
    cache.flatMap { cache =>
      underlying.remove(loginInfo).map { _ =>
        sender ! Success(Unit)
        cache.copy(
          entities = cache.entities - loginInfo,
          used = cache.used - loginInfo
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
