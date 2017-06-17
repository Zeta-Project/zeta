package de.htwg.zeta.persistence.actorCache

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
import com.mohiva.play.silhouette.api.util.PasswordInfo
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Add
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Find
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Remove
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Save
import de.htwg.zeta.persistence.actorCache.PasswordInfoCacheActor.Update
import de.htwg.zeta.persistence.general.PasswordInfoPersistence

/**
 * Actor Cache Implementation of PasswordInfoPersistence.
 */
class ActorCachePasswordInfoPersistence(
    system: ActorSystem,
    underlying: PasswordInfoPersistence,
    numberActorsPerEntityType: Int,
    cacheDuration: FiniteDuration,
    implicit val timeout: Timeout
) extends PasswordInfoPersistence {


  private def hashMapping: ConsistentHashMapping = {
    case Add(loginInfo, _) => loginInfo
    case Find(loginInfo) => loginInfo
    case Update(loginInfo, _) => loginInfo
    case Save(loginInfo, _) => loginInfo
    case Remove(loginInfo) => loginInfo
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
  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    (router ? Add(loginInfo, authInfo)).flatMap {
      case Success(authInfo: PasswordInfo) => Future.successful(authInfo)
      case Failure(e) => Future.failed(e)
    }
  }

  /** Finds the auth info which is linked to the specified login info.
   *
   * @param loginInfo The linked login info.
   * @return The found auth info or None if no auth info could be found for the given login info.
   */
  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    (router ? Find(loginInfo)).flatMap {
      case Success(authInfo: Option[PasswordInfo]) => Future.successful(authInfo)
      case Failure(e) => Future.failed(e)
    }
  }

  /** Updates the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be updated.
   * @param authInfo  The auth info to update.
   * @return The updated auth info.
   */
  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    (router ? Update(loginInfo, authInfo)).flatMap {
      case Success(authInfo: PasswordInfo) => Future.successful(authInfo)
      case Failure(e) => Future.failed(e)
    }
  }

  /** Saves the auth info for the given login info. This method either adds the auth info if it doesn't exists or it updates the auth info if it already exists.
   *
   * @param loginInfo The login info for which the auth info should be saved.
   * @param authInfo  The auth info to save.
   * @return The saved auth info.
   */
  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    (router ? Save(loginInfo, authInfo)).flatMap {
      case Success(authInfo: PasswordInfo) => Future.successful(authInfo)
      case Failure(e) => Future.failed(e)
    }
  }

  /** Removes the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be removed.
   * @return A future to wait for the process to be completed.
   */
  override def remove(loginInfo: LoginInfo): Future[Unit] = {
    (router ? Remove(loginInfo)).flatMap {
      case Success(Unit) => Future.successful(())
      case Failure(e) => Future.failed(e)
    }
  }

  /** Read all LoginInfo's
   *
   * @return all LoginInfo's
   */
  override def readAllKeys(): Future[Set[LoginInfo]] = {
    underlying.readAllKeys()
  }

}
