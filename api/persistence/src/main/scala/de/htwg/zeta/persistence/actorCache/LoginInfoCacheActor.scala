package de.htwg.zeta.persistence.actorCache


import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.util.Failure
import scala.util.Success

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Cancellable
import akka.actor.Props
import com.mohiva.play.silhouette.api.LoginInfo
import de.htwg.zeta.persistence.actorCache.LoginInfoCacheActor.Cache
import de.htwg.zeta.persistence.actorCache.LoginInfoCacheActor.CleanUp
import de.htwg.zeta.persistence.actorCache.LoginInfoCacheActor.Create
import de.htwg.zeta.persistence.actorCache.LoginInfoCacheActor.Delete
import de.htwg.zeta.persistence.actorCache.LoginInfoCacheActor.Found
import de.htwg.zeta.persistence.actorCache.LoginInfoCacheActor.Read
import de.htwg.zeta.persistence.actorCache.LoginInfoCacheActor.Update
import de.htwg.zeta.persistence.general.LoginInfoPersistence


private[actorCache] object LoginInfoCacheActor {

  case class Create(loginInfo: LoginInfo, userId: UUID)

  case class Read(loginInfo: LoginInfo)

  case class Update(old: LoginInfo, updated: LoginInfo)

  case class Delete(loginInfo: LoginInfo)

  private case object CleanUp

  private case class Found(userId: UUID, inCache: Boolean)

  private case class Cache(entities: Map[LoginInfo, UUID], used: Set[LoginInfo])

  def props(underlying: LoginInfoPersistence, cacheDuration: FiniteDuration): Props = Props(new LoginInfoCacheActor(underlying, cacheDuration))

}

private[actorCache] class LoginInfoCacheActor(underlying: LoginInfoPersistence, cacheDuration: FiniteDuration) extends Actor with ActorLogging {

  private val cleanUpJob: Cancellable = context.system.scheduler.schedule(cacheDuration, cacheDuration, self, CleanUp)

  override def receive: Receive = state(Future.successful(Cache(Map.empty, Set.empty)))

  private def state(cache: Future[Cache]): Receive = {
    case Create(loginInfo, userId) => context.become(state(create(cache, loginInfo, userId, sender)))
    case Read(loginInfo) => context.become(state(read(cache, loginInfo, sender)))
    case Update(old, updated) => context.become(state(update(cache, old, updated, sender)))
    case Delete(loginInfo) => context.become(state(delete(cache, loginInfo, sender)))
    case CleanUp => context.become(state(cleanUp(cache)))
  }

  private def create(cache: Future[Cache], loginInfo: LoginInfo, userId: UUID, sender: ActorRef): Future[Cache] = {
    log.info(s"creating - ${loginInfo.toString}") // scalastyle:ignore multiple.string.literals
    cache.flatMap { cache =>
      underlying.create(loginInfo, userId).map { _ =>
        sender ! Success(Unit)
        cache.copy(
          entities = cache.entities + (loginInfo -> userId),
          used = cache.used + loginInfo
        )
      }.recover { case e: Exception =>
        sender ! Failure(e)
        cache
      }
    }
  }

  private def read(cache: Future[Cache], loginInfo: LoginInfo, sender: ActorRef): Future[Cache] = {
    log.info(s"reading - ${loginInfo.toString}")
    cache.flatMap { cache =>
      cache.entities.get(loginInfo).fold[Future[Found]] {
        underlying.read(loginInfo).map(Found(_, inCache = false))
      } { file =>
        Future(Found(file, inCache = true))
      }.map { found =>
        sender ! Success(found.userId)
        if (found.inCache) {
          cache.copy(used = cache.used + loginInfo)
        } else {
          cache.copy(
            entities = cache.entities + (loginInfo -> found.userId),
            used = cache.used + loginInfo
          )
        }
      }.recover { case e: Exception =>
        sender ! Failure(e)
        cache
      }
    }
  }

  private def update(cache: Future[Cache], old: LoginInfo, updated: LoginInfo, sender: ActorRef): Future[Cache] = {
    log.info(s"updating - ${old.toString} to ${updated.toString}")
    cache.flatMap { cache =>
      underlying.update(old, updated).map { _ =>
        sender ! Success(Unit)
        cache.copy(
          entities = cache.entities - old,
          used = cache.used - old
        )
      }.recover { case e: Exception =>
        sender ! Failure(e)
        cache
      }
    }
  }

  private def delete(cache: Future[Cache], loginInfo: LoginInfo, sender: ActorRef): Future[Cache] = {
    log.info(s"deleting - ${loginInfo.toString}")
    cache.flatMap { cache =>
      underlying.delete(loginInfo).map { _ =>
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
    log.info("cleaning cache")
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
