package de.htwg.zeta.server.silhouette

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo
import de.htwg.zeta.persistence.authInfo.ZetaLoginInfo
import de.htwg.zeta.persistence.general.LoginInfoRepository

/**
 * Persistence to save the LoginInfo and the User-Id it belongs to.
 */
class SilhouetteLoginInfoDao @Inject()(
    repo: LoginInfoRepository
) {

  /** Create a LoginInfo.
   *
   * @param loginInfo The LoginInfo.
   * @param id        The id of the user.
   * @return Unit-Future, when successful.
   */
  def create(loginInfo: LoginInfo, id: UUID): Future[Unit] = repo.create(ZetaLoginInfo(loginInfo), id)

  /** Get a user that matches the specified LoginInfo.
   *
   * @param loginInfo The LoginInfo.
   * @return The id of the User.
   */
  def read(loginInfo: LoginInfo): Future[UUID] = repo.read(ZetaLoginInfo(loginInfo))

}
