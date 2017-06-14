package de.htwg.zeta.persistence.mongo

import java.util.UUID

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import reactivemongo.api.DefaultDB


class MongoLoginInfoPersistence(database: Future[DefaultDB]) extends LoginInfoPersistence {

  /** Create a LoginInfo.
   *
   * @param loginInfo The LoginInfo.
   * @param id        The id of the user.
   * @return Unit-Future, when successful.
   */
  override def create(loginInfo: LoginInfo, id: UUID): Future[Unit] = {
    null // TODO
  }

  /**
   * Get a user that matches the specified LoginInfo.
   *
   * @param loginInfo The LoginInfo.
   * @return The id of the User.
   */
  override def read(loginInfo: LoginInfo): Future[UUID] = {
    null // TODO
  }

  /** Update a LoginInfo.
   *
   * @param old     The LoginInfo to update.
   * @param updated The updated LoginInfo.
   * @return Unit-Future
   */
  override def update(old: LoginInfo, updated: LoginInfo): Future[Unit] = {
    null // TODO
  }

  /** Delete a LoginInfo.
   *
   * @param loginInfo LoginInfo
   * @return Unit-Future
   */
  override def delete(loginInfo: LoginInfo): Future[Unit] = {
    null // TODO
  }

}
