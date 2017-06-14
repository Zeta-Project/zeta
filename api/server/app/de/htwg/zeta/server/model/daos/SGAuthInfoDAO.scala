package de.htwg.zeta.server.model.daos

import javax.inject.Inject

import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import de.htwg.zeta.common.models.document.PasswordInfoEntity
import de.htwg.zeta.server.util.auth.RepositoryFactory
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise

import de.htwg.zeta.common.models.User

/**
 * An implementation of the auth info DAO which stores the data in the Sync Gateway DB
 */
class SGAuthInfoDAO @Inject() (implicit repositoryFactory: RepositoryFactory) extends DelegableAuthInfoDAO[PasswordInfo] {
  val repository = repositoryFactory.forAdministrator

  def getPasswordInfoId(loginInfo: LoginInfo) = s"PasswordInfoEntity-${User.getUserId(loginInfo)}"

  /**
   * Finds the auth info which is linked with the specified login info.
   *
   * @param loginInfo The linked login info.
   * @return The retrieved auth info or None if no auth info could be retrieved for the given login info.
   */
  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    val p = Promise[Option[PasswordInfo]]

    val id = getPasswordInfoId(loginInfo)

    repository.get[PasswordInfoEntity](id).map { entity =>
      p.success(Some(entity.passwordInfo))
    }.recover {
      case e: Exception => p.success(None)
    }

    p.future
  }

  /**
   * Adds new auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be added.
   * @param authInfo The auth info to add.
   * @return The added auth info.
   */
  def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    val p = Promise[PasswordInfo]

    val owner = User.getUserId(loginInfo)
    val entity = PasswordInfoEntity(owner, authInfo)

    repository.create[PasswordInfoEntity](entity).map { result =>
      p.success(authInfo)
    }.recover {
      case e: Exception => p.failure(e)
    }

    p.future
  }

  /**
   * Updates the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be updated.
   * @param authInfo The auth info to update.
   * @return The updated auth info.
   */
  def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    val p = Promise[PasswordInfo]

    val id = getPasswordInfoId(loginInfo)

    val op = for {
      saved <- repository.get[PasswordInfoEntity](id)
      updated <- repository.update[PasswordInfoEntity](saved.copy(passwordInfo = authInfo))
    } yield updated

    op.map { value =>
      p.success(authInfo)
    }.recover {
      case e: Exception => p.failure(e)
    }

    p.future
  }

  /**
   * Saves the auth info for the given login info.
   *
   * This method either adds the auth info if it doesn't exists or it updates the auth info
   * if it already exists.
   *
   * @param loginInfo The login info for which the auth info should be saved.
   * @param authInfo The auth info to save.
   * @return The saved auth info.
   */
  def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    find(loginInfo).flatMap {
      case Some(_) => update(loginInfo, authInfo)
      case None => add(loginInfo, authInfo)
    }
  }

  /**
   * Removes the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be removed.
   * @return A future to wait for the process to be completed.
   */
  def remove(loginInfo: LoginInfo): Future[Unit] = {
    val p = Promise[Unit]

    val id = getPasswordInfoId(loginInfo)

    repository.delete(id).map { result =>
      p.success(())
    }.recover {
      case e: Exception => p.failure(e)
    }

    p.future
  }
}
