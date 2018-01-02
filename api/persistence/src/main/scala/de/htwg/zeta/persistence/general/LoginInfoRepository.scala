package de.htwg.zeta.persistence.general

import java.util.UUID

import scala.concurrent.Future

import de.htwg.zeta.persistence.authInfo.ZetaLoginInfo

/**
 * Persistence to save the LoginInfo and the User-Id it belongs to.
 */
trait LoginInfoRepository {

  /** Create a LoginInfo.
   *
   * @param loginInfo The LoginInfo.
   * @param id        The id of the user.
   * @return Unit-Future, when successful.
   */
  def create(loginInfo: ZetaLoginInfo, id: UUID): Future[Unit]

  /** Get a user that matches the specified LoginInfo.
   *
   * @param loginInfo The LoginInfo.
   * @return The id of the User.
   */
  def read(loginInfo: ZetaLoginInfo): Future[UUID]

  /** Update a LoginInfo.
   *
   * @param old     The LoginInfo to update.
   * @param updated The updated LoginInfo.
   * @return Unit-Future
   */
  def update(old: ZetaLoginInfo, updated: ZetaLoginInfo): Future[Unit]

  /** Delete a LoginInfo.
   *
   * @param loginInfo LoginInfo
   * @return Unit-Future
   */
  def delete(loginInfo: ZetaLoginInfo): Future[Unit]

  /** Read all LoginInfo's.
   *
   * @return Future containing all LoginInfo's
   */
  def readAllKeys(): Future[Set[ZetaLoginInfo]]

}
