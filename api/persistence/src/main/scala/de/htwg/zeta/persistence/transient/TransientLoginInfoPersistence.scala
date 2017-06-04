package de.htwg.zeta.persistence.transient

import java.util.UUID

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo
import de.htwg.zeta.persistence.general.LoginInfoPersistence

/**
 * TODO
 */
class TransientLoginInfoPersistence extends LoginInfoPersistence {

  private val cache: TrieMap[LoginInfo, UUID] = TrieMap.empty

  /** Create a LoginInfo.
   *
   * @param loginInfo The LoginInfo.
   * @param id        The id of the user.
   * @return Unit-Future, when successful.
   */
  override def create(loginInfo: LoginInfo, id: UUID): Future[Unit] = {
    cache.putIfAbsent(loginInfo, id).fold {
      Future.successful(())
    } { _ =>
      Future.failed(new IllegalStateException)
    }
  }

  /**
   * Get a user that matches the specified LoginInfo.
   *
   * @param loginInfo The LoginInfo.
   * @return The id of the User.
   */
  override def read(loginInfo: LoginInfo): Future[UUID] = {
    cache.get(loginInfo).fold[Future[UUID]] {
      Future.failed(new NoSuchElementException)
    } { id =>
      Future.successful(id)
    }
  }

  /** Update a LoginInfo.
   *
   * @param old     The LoginInfo to update.
   * @param updated The updated LoginInfo.
   * @return Unit-Future
   */
  override def update(old: LoginInfo, updated: LoginInfo): Future[Unit] = {
    cache.remove(old).fold[Future[Unit]] {
      Future.failed(new NoSuchElementException)
    } { id =>
      cache.putIfAbsent(updated, id).fold {
        Future.successful(())
      } { _ =>
        cache.put(old, id)
        Future.failed(new IllegalStateException())
      }
    }
  }

  /** Delete a LoginInfo.
   *
   * @param loginInfo LoginInfo
   * @return Unit-Future
   */
  override def delete(loginInfo: LoginInfo): Future[Unit] = {
    cache.remove(loginInfo).fold[Future[Unit]] {
      Future.failed(new NoSuchElementException)
    } { _ =>
      Future.successful(())
    }
  }
}
