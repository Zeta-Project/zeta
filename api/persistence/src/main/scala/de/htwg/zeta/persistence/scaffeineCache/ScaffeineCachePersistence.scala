package de.htwg.zeta.persistence.scaffeineCache

import java.util.UUID
import java.util.concurrent.TimeUnit

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration

import com.github.blemale.scaffeine.Cache
import com.github.blemale.scaffeine.Scaffeine
import de.htwg.zeta.persistence.general.Persistence
import models.entity.Entity


/** Cache-Layer for the persistence, implemented with Scaffeine-Caching library.
 *
 * @param underlaying         the underlaying Persistence
 * @param keepInCacheDuration the length of time after an entry is created that it should be automatically removed
 * @param maximumSize         the maximum size of the cache
 * @tparam E type of the entity
 */
case class ScaffeineCachePersistence[E <: Entity]( // scalastyle:ignore
    underlaying: Persistence[E],
    keepInCacheDuration: Duration = Duration(10, TimeUnit.MINUTES), // scalastyle:ignore
    maximumSize: Int = 1000 // scalastyle:ignore
) extends Persistence[E] {

  private val cache: Cache[UUID, E] =
    Scaffeine()
      .recordStats()
      .expireAfterWrite(keepInCacheDuration)
      .maximumSize(maximumSize)
      .build()

  /** Create a new entity.
   *
   * @param entity the entity to save
   * @return Future, which can fail
   */
  override def create(entity: E): Future[E] = {
    underlaying.create(entity).flatMap(entity => {
      cache.put(entity.id, entity)
      Future.successful(entity)
    })
  }

  /** Get a single entity.
   *
   * @param id The id of the entity
   * @return Future which resolve with the entity and can fail
   */
  override def read(id: UUID): Future[E] = {
    cache.getIfPresent(id).fold(
      underlaying.read(id).flatMap { entity =>
        cache.put(id, entity)
        Future.successful(entity)
      }
    )(entity =>
      Future.successful(entity)
    )
  }

  /** Delete a entity.
   *
   * @param id The id of the entity to delete
   * @return Future, which can fail
   */
  override def delete(id: UUID): Future[Unit] = {
    underlaying.delete(id).flatMap { _ =>
      cache.invalidate(id)
      Future.successful(())
    }
  }

  /** Get the id's of all entity.
   *
   * @return Future containing all id's of the entity type, can fail
   */
  override def readAllIds(): Future[Set[UUID]] = {
    underlaying.readAllIds()
  }

  /** Update a entity.
   *
   * @param entity The updated entity
   * @return Future containing the updated entity
   */
  override private[persistence] def update(entity: E): Future[E] = {
    underlaying.update(entity).flatMap(entity => {
      cache.put(entity.id, entity)
      Future.successful(entity)
    })
  }

}
