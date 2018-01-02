package de.htwg.zeta.persistence.transient

import java.util.UUID
import javax.inject.Singleton

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future

import de.htwg.zeta.persistence.authInfo.ZetaLoginInfo
import de.htwg.zeta.persistence.general.LoginInfoRepository

/**
 * Transient Implementation of LoginInfoPersistence.
 */
@Singleton
class TransientLoginInfoRepository extends LoginInfoRepository {

  private val cache: TrieMap[ZetaLoginInfo, UUID] = TrieMap.empty

  /** Create a LoginInfo.
   *
   * @param loginInfo The LoginInfo.
   * @param id        The id of the user.
   * @return Unit-Future, when successful.
   */
  override def create(loginInfo: ZetaLoginInfo, id: UUID): Future[Unit] = {
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
  override def read(loginInfo: ZetaLoginInfo): Future[UUID] = {
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
  override def update(old: ZetaLoginInfo, updated: ZetaLoginInfo): Future[Unit] = {
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
  override def delete(loginInfo: ZetaLoginInfo): Future[Unit] = {
    cache.remove(loginInfo).fold[Future[Unit]] {
      Future.failed(new NoSuchElementException)
    } { _ =>
      Future.successful(())
    }
  }

  /** Get all LoginInfo's.
   *
   * @return Future containing all LoginInfo's
   */
  override def readAllKeys(): Future[Set[ZetaLoginInfo]] = {
    Future.successful(cache.keys.toSet)
  }

}
