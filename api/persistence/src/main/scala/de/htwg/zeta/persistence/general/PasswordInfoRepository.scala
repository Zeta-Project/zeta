package de.htwg.zeta.persistence.general

import scala.concurrent.Future

import de.htwg.zeta.persistence.authInfo.ZetaLoginInfo
import de.htwg.zeta.persistence.authInfo.ZetaPasswordInfo

/**
 * Persistence for the PasswordInfo.
 */
trait PasswordInfoRepository {

  /** Adds new auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be added.
   * @param authInfo  The auth info to add.
   * @return The added auth info.
   */
  def add(loginInfo: ZetaLoginInfo, authInfo: ZetaPasswordInfo): Future[ZetaPasswordInfo]

  /** Finds the auth info which is linked to the specified login info.
   *
   * @param loginInfo The linked login info.
   * @return The found auth info or None if no auth info could be found for the given login info.
   */
  def find(loginInfo: ZetaLoginInfo): Future[Option[ZetaPasswordInfo]]

  /** Updates the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be updated.
   * @param authInfo  The auth info to update.
   * @return The updated auth info.
   */
  def update(loginInfo: ZetaLoginInfo, authInfo: ZetaPasswordInfo): Future[ZetaPasswordInfo]

  /** Saves the auth info for the given login info. This method either adds the auth info if it doesn't exists or it updates the auth info if it already exists.
   *
   * @param loginInfo The login info for which the auth info should be saved.
   * @param authInfo  The auth info to save.
   * @return The saved auth info.
   */
  def save(loginInfo: ZetaLoginInfo, authInfo: ZetaPasswordInfo): Future[ZetaPasswordInfo]

  /** Removes the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be removed.
   * @return A future to wait for the process to be completed.
   */
  def remove(loginInfo: ZetaLoginInfo): Future[Unit]

  /** Read all LoginInfo's
   *
   * @return all LoginInfo's
   */
  def readAllKeys(): Future[Set[ZetaLoginInfo]]

}
