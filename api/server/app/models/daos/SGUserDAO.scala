package models.daos

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import models.document.AllUsers
import models.document.Settings
import models.document.UserEntity
import models.session.Account
import models.session.SyncGatewayAccount
import play.api.libs.ws.WSClient
import rx.lang.scala.Notification.OnError
import rx.lang.scala.Notification.OnNext
import de.htwg.zeta.server.utils.auth.RepositoryFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise

class SGUserDAO @Inject() (implicit wSClient: WSClient, repositoryFactory: RepositoryFactory) extends UserDAO {
  val repository = repositoryFactory.forAdministrator
  val accounts = SyncGatewayAccount()

  def getUserId(loginInfo: LoginInfo) = s"UserEntity-${User.getUserId(loginInfo)}"

  def getUserId(user: User) = s"UserEntity-${User.getUserId(user.loginInfo)}"

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  override def find(loginInfo: LoginInfo): Future[Option[User]] = {
    val p = Promise[Option[User]]

    val id = getUserId(loginInfo)

    repository.get[UserEntity](id).map { entity =>
      p.success(Some(entity.user))
    }.recover {
      case e: Exception => p.success(None)
    }

    p.future
  }

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  override def find(userID: UUID): Future[Option[User]] = {
    val p = Promise[Option[User]]

    repository.query[UserEntity](AllUsers()).filter { entity =>
      entity.user.userID == userID
    }.first.materialize.subscribe(n => n match {
      case OnError(err) => p.success(None)
      case OnNext(entity) => p.success(Some(entity.user))
    })

    p.future
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  override def save(user: User): Future[User] = {
    if (user.activated) {
      updateUser(user)
    } else {
      createUser(user)
    }
  }

  /**
   * Create an user.
   *
   * @param user The user to create
   * @return The created user
   */
  def createUser(user: User): Future[User] = {
    val p = Promise[User]

    val id = User.getUserId(user)
    val account = Account(name = id)

    val op = for {
      // create an sync gateway account
      account <- accounts.create(account)
      // create a document with the internal settings for the user
      settings <- repository.create[Settings](Settings(id))
      // create an document which represent the user
      created <- repository.create[UserEntity](UserEntity(id, user))
    } yield created

    op.map { entity =>
      p.success(entity.user)
    }.recover {
      case e: Exception => p.failure(e)
    }

    p.future
  }

  /**
   * Update an existing user.
   *
   * @param user The user to update
   * @return The updated user
   */
  def updateUser(user: User): Future[User] = {
    val p = Promise[User]

    val op = for {
      saved <- repository.get[UserEntity](getUserId(user))
      updated <- repository.update[UserEntity](saved.copy(user = user))
    } yield updated

    op.map { entity =>
      p.success(entity.user)
    }.recover {
      case e: Exception => p.failure(e)
    }

    p.future
  }
}
