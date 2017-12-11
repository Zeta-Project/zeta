package de.htwg.zeta.persistence.transient

import javax.inject.Singleton

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import de.htwg.zeta.persistence.general.PasswordInfoRepository

/**
 * Transient implementation of the PasswordInfoPersistence.
 */
@Singleton
class TransientPasswordInfoRepository extends PasswordInfoRepository {

  private val cache: TrieMap[LoginInfo, PasswordInfo] = TrieMap.empty

  /** Finds the auth info which is linked to the specified login info.
   *
   * @param loginInfo The linked login info.
   * @return The found auth info or None if no auth info could be found for the given login info.
   */
  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    Future.successful(cache.get(loginInfo))
  }

  /** Adds new auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be added.
   * @param authInfo  The auth info to add.
   * @return The added auth info.
   */
  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    cache.putIfAbsent(loginInfo, authInfo).fold {
      Future.successful(authInfo)
    } { _ =>
      Future.failed(new IllegalStateException)
    }
  }

  /** Updates the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be updated.
   * @param authInfo  The auth info to update.
   * @return The updated auth info.
   */
  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    cache.replace(loginInfo, authInfo).fold[Future[PasswordInfo]] {
      Future.failed(new IllegalStateException)
    } { _ =>
      Future.successful(authInfo)
    }
  }

  /** Saves the auth info for the given login info. This method either adds the auth info if it doesn't exists or it updates the auth info if it already exists.
   *
   * @param loginInfo The login info for which the auth info should be saved.
   * @param authInfo  The auth info to save.
   * @return The saved auth info.
   */
  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    cache.update(loginInfo, authInfo)
    Future.successful(authInfo)
  }

  /** Removes the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be removed.
   * @return A future to wait for the process to be completed.
   */
  override def remove(loginInfo: LoginInfo): Future[Unit] = {
    cache.remove(loginInfo)
    Future.successful(())
  }

  /** Read all LoginInfo's
   *
   * @return all LoginInfo's
   */
  override def readAllKeys(): Future[Set[LoginInfo]] = {
    Future.successful(cache.keys.toSet)
  }

}
