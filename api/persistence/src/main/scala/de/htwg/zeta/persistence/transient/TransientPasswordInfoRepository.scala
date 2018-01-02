package de.htwg.zeta.persistence.transient

import javax.inject.Singleton

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future

import de.htwg.zeta.persistence.authInfo.ZetaPasswordInfo
import de.htwg.zeta.persistence.authInfo.ZetaLoginInfo
import de.htwg.zeta.persistence.general.PasswordInfoRepository

/**
 * Transient implementation of the PasswordInfoPersistence.
 */
@Singleton
class TransientPasswordInfoRepository extends PasswordInfoRepository {

  private val cache: TrieMap[ZetaLoginInfo, ZetaPasswordInfo] = TrieMap.empty

  /** Finds the auth info which is linked to the specified login info.
   *
   * @param loginInfo The linked login info.
   * @return The found auth info or None if no auth info could be found for the given login info.
   */
  override def find(loginInfo: ZetaLoginInfo): Future[Option[ZetaPasswordInfo]] = {
    Future.successful(cache.get(loginInfo))
  }

  /** Adds new auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be added.
   * @param authInfo  The auth info to add.
   * @return The added auth info.
   */
  override def add(loginInfo: ZetaLoginInfo, authInfo: ZetaPasswordInfo): Future[ZetaPasswordInfo] = {
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
  override def update(loginInfo: ZetaLoginInfo, authInfo: ZetaPasswordInfo): Future[ZetaPasswordInfo] = {
    cache.replace(loginInfo, authInfo).fold[Future[ZetaPasswordInfo]] {
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
  override def save(loginInfo: ZetaLoginInfo, authInfo: ZetaPasswordInfo): Future[ZetaPasswordInfo] = {
    cache.update(loginInfo, authInfo)
    Future.successful(authInfo)
  }

  /** Removes the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be removed.
   * @return A future to wait for the process to be completed.
   */
  override def remove(loginInfo: ZetaLoginInfo): Future[Unit] = {
    cache.remove(loginInfo)
    Future.successful(())
  }

  /** Read all LoginInfo's
   *
   * @return all LoginInfo's
   */
  override def readAllKeys(): Future[Set[ZetaLoginInfo]] = {
    Future.successful(cache.keys.toSet)
  }

}
