package de.htwg.zeta.persistence.actorCache

import java.util.UUID
import javax.inject.Singleton
import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.util.Failure
import scala.util.Success

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.routing.ConsistentHashingPool
import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
import akka.util.Timeout
import com.mohiva.play.silhouette.api.LoginInfo
import de.htwg.zeta.persistence.actorCache.LoginInfoCacheActor.Create
import de.htwg.zeta.persistence.actorCache.LoginInfoCacheActor.Delete
import de.htwg.zeta.persistence.actorCache.LoginInfoCacheActor.Read
import de.htwg.zeta.persistence.actorCache.LoginInfoCacheActor.Update
import de.htwg.zeta.persistence.general.LoginInfoPersistence

/**
 * Actor Cache Implementation of LoginInfoPersistence.
 */
@Singleton
class ActorCacheLoginInfoPersistence @Inject()(
    underlying: LoginInfoPersistence,
    system: ActorSystem,
    numberActorsPerEntityType: Int,
    cacheDuration: FiniteDuration,
    implicit val timeout: Timeout
) extends LoginInfoPersistence {

  private def hashMapping: ConsistentHashMapping = {
    case Create(loginInfo, _) => loginInfo.hashCode
    case Read(loginInfo) => loginInfo.hashCode
    case Update(loginInfo, _) => loginInfo.hashCode
    case Delete(loginInfo) => loginInfo.hashCode
  }

  private val router: ActorRef = system.actorOf(
    ConsistentHashingPool(
      nrOfInstances = numberActorsPerEntityType,
      hashMapping = hashMapping
    ).props(
      LoginInfoCacheActor.props(underlying, cacheDuration)
    ),
    "LoginInfo"
  )

  /** Create a LoginInfo.
   *
   * @param loginInfo The LoginInfo.
   * @param id        The id of the user.
   * @return Unit-Future, when successful.
   */
  override def create(loginInfo: LoginInfo, id: UUID): Future[Unit] = {
    (router ? Create(loginInfo, id)).flatMap {
      case Success(()) => Future.successful(())
      case Failure(e) => Future.failed(e)
    }
  }

  /** Get a user that matches the specified LoginInfo.
   *
   * @param loginInfo The LoginInfo.
   * @return The id of the User.
   */
  override def read(loginInfo: LoginInfo): Future[UUID] = {
    (router ? Read(loginInfo)).flatMap {
      case Success(userId: UUID) => Future.successful(userId)
      case Failure(e) => Future.failed(e)
    }
  }

  /** Update a LoginInfo.
   *
   * @param old     The LoginInfo to update.
   * @param updated The updated LoginInfo.
   * @return Unit-Future
   */
  override def update(old: LoginInfo, updated: LoginInfo): Future[Unit] = {
    (router ? Update(old, updated)).flatMap {
      case Success(()) => Future.successful(())
      case Failure(e) => Future.failed(e)
    }
  }

  /** Delete a LoginInfo.
   *
   * @param loginInfo LoginInfo
   * @return Unit-Future
   */
  override def delete(loginInfo: LoginInfo): Future[Unit] = {
    (router ? Delete(loginInfo)).flatMap {
      case Success(()) => Future.successful(())
      case Failure(e) => Future.failed(e)
    }
  }

  /** Read all LoginInfo's.
   *
   * @return Future containing all LoginInfo's
   */
  override def readAllKeys(): Future[Set[LoginInfo]] = {
    underlying.readAllKeys()
  }

}
