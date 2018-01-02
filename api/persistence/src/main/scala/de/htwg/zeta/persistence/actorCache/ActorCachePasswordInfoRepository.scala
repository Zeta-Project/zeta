package de.htwg.zeta.persistence.actorCache

import javax.inject.Inject
import javax.inject.Singleton

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
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Add
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Find
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Remove
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Save
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Update
import de.htwg.zeta.persistence.authInfo.ZetaLoginInfo
import de.htwg.zeta.persistence.authInfo.ZetaPasswordInfo
import de.htwg.zeta.persistence.general.PasswordInfoRepository

/**
 * Actor Cache Implementation of PasswordInfoPersistence.
 */
@Singleton
class ActorCachePasswordInfoRepository @Inject()(
    underlying: PasswordInfoRepository,
    system: ActorSystem,
    numberActorsPerEntityType: Int,
    cacheDuration: FiniteDuration,
    implicit val timeout: Timeout
) extends PasswordInfoRepository {


  private def hashMapping: ConsistentHashMapping = {
    case Add(loginInfo, _) => loginInfo.hashCode
    case Find(loginInfo) => loginInfo.hashCode
    case Update(loginInfo, _) => loginInfo.hashCode
    case Save(loginInfo, _) => loginInfo.hashCode
    case Remove(loginInfo) => loginInfo.hashCode
  }

  private val router: ActorRef = system.actorOf(
    ConsistentHashingPool(
      nrOfInstances = numberActorsPerEntityType,
      hashMapping = hashMapping
    ).props(
      PasswordInfoCacheActor.props(underlying, cacheDuration)
    ),
    "PasswordInfo"
  )

  /** Adds new auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be added.
   * @param authInfo  The auth info to add.
   * @return The added auth info.
   */
  override def add(loginInfo: ZetaLoginInfo, authInfo: ZetaPasswordInfo): Future[ZetaPasswordInfo] = {
    (router ? Add(loginInfo, authInfo)).flatMap {
      case Success(authInfo: ZetaPasswordInfo) => Future.successful(authInfo)
      case Failure(e) => Future.failed(e)
    }
  }

  /** Finds the auth info which is linked to the specified login info.
   *
   * @param loginInfo The linked login info.
   * @return The found auth info or None if no auth info could be found for the given login info.
   */
  override def find(loginInfo: ZetaLoginInfo): Future[Option[ZetaPasswordInfo]] = {
    (router ? Find(loginInfo)).flatMap {
      case Success(authInfo: Option[ZetaPasswordInfo]) => Future.successful(authInfo)
      case Failure(e) => Future.failed(e)
    }
  }

  /** Updates the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be updated.
   * @param authInfo  The auth info to update.
   * @return The updated auth info.
   */
  override def update(loginInfo: ZetaLoginInfo, authInfo: ZetaPasswordInfo): Future[ZetaPasswordInfo] = {
    (router ? Update(loginInfo, authInfo)).flatMap {
      case Success(authInfo: ZetaPasswordInfo) => Future.successful(authInfo)
      case Failure(e) => Future.failed(e)
    }
  }

  /** Saves the auth info for the given login info. This method either adds the auth info if it doesn't exists or it updates the auth info if it already exists.
   *
   * @param loginInfo The login info for which the auth info should be saved.
   * @param authInfo  The auth info to save.
   * @return The saved auth info.
   */
  override def save(loginInfo: ZetaLoginInfo, authInfo: ZetaPasswordInfo): Future[ZetaPasswordInfo] = {
    (router ? Save(loginInfo, authInfo)).flatMap {
      case Success(authInfo: ZetaPasswordInfo) => Future.successful(authInfo)
      case Failure(e) => Future.failed(e)
    }
  }

  /** Removes the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be removed.
   * @return A future to wait for the process to be completed.
   */
  override def remove(loginInfo: ZetaLoginInfo): Future[Unit] = {
    (router ? Remove(loginInfo)).flatMap {
      case Success(()) => Future.successful(())
      case Failure(e) => Future.failed(e)
    }
  }

  /** Read all LoginInfo's
   *
   * @return all LoginInfo's
   */
  override def readAllKeys(): Future[Set[ZetaLoginInfo]] = {
    underlying.readAllKeys()
  }

}
