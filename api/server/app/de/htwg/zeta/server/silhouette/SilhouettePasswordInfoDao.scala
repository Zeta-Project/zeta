package de.htwg.zeta.server.silhouette

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import de.htwg.zeta.persistence.authInfo.ZetaLoginInfo
import de.htwg.zeta.persistence.authInfo.ZetaPasswordInfo
import de.htwg.zeta.persistence.general.PasswordInfoRepository
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Persistence for the PasswordInfo.
 */
class SilhouettePasswordInfoDao (
    passwordInfoRepo: PasswordInfoRepository
) extends DelegableAuthInfoDAO[PasswordInfo] {

  /** Adds new auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be added.
   * @param authInfo  The auth info to add.
   * @return The added auth info.
   */
  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    passwordInfoRepo.add(ZetaLoginInfo(loginInfo), ZetaPasswordInfo(authInfo)).map(_.toPasswordInfo())

  /** Finds the auth info which is linked to the specified login info.
   *
   * @param loginInfo The linked login info.
   * @return The found auth info or None if no auth info could be found for the given login info.
   */
  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] =
    passwordInfoRepo.find(ZetaLoginInfo(loginInfo)).map(_.map(_.toPasswordInfo()))

  /** Updates the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be updated.
   * @param authInfo  The auth info to update.
   * @return The updated auth info.
   */
  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    passwordInfoRepo.update(ZetaLoginInfo(loginInfo), ZetaPasswordInfo(authInfo)).map(_.toPasswordInfo())

  /** Saves the auth info for the given login info. This method either adds the auth info if it doesn't exists or it updates the auth info if it already exists.
   *
   * @param loginInfo The login info for which the auth info should be saved.
   * @param authInfo  The auth info to save.
   * @return The saved auth info.
   */
  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    passwordInfoRepo.save(ZetaLoginInfo(loginInfo), ZetaPasswordInfo(authInfo)).map(_.toPasswordInfo())

  /** Removes the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be removed.
   * @return A future to wait for the process to be completed.
   */
  override def remove(loginInfo: LoginInfo): Future[Unit] =
    passwordInfoRepo.remove(ZetaLoginInfo(loginInfo))

}
