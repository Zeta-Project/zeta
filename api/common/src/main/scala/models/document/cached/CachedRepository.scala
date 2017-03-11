package models.document.http

import models.document._
import play.api.libs.json._
import rx.lang.scala.Observable

import scala.concurrent.{ Future, Promise }
import scala.reflect._

object CachedRepository {
  def apply(remote: Repository): Repository with Cache = {
    new CachedRepository(remote)
  }
}

/**
 * Cached Repository which use Scaffine to cache documents
 */
class CachedRepository(remote: Repository) extends Repository with Cache {
  import scala.concurrent.ExecutionContext.Implicits.global
  import com.github.blemale.scaffeine.{ Cache, Scaffeine }
  import scala.concurrent.duration._

  val cache: Cache[String, Document] =
    Scaffeine()
      .recordStats()
      .expireAfterWrite(10.minutes)
      .maximumSize(100)
      .build[String, Document]()

  /**
   * Invalidate a cache entry
   *
   * @param id The id of the invalid entry
   * @return Future which resolve after invalidation
   */
  override def invalidate(id: String): Future[Unit] = {
    cache.invalidate(id)
    Future.successful()
  }

  /**
   * Create a new document
   *
   * @param doc The Document to save
   * @return Future which resolve with the created document
   */
  override def create[T: ClassTag](doc: Document)(implicit w: Writes[Document]): Future[T] = remote.create(doc)

  /**
   * Update a document
   *
   * @param doc The Document to update
   * @return Future which resolve with the updated document
   */
  override def update[T: ClassTag](doc: Document)(implicit w: Writes[Document]): Future[T] = {
    // update the cache entry
    cache.getIfPresent(doc.id) match {
      case Some(x) => cache.put(doc.id, doc)
      case None => // ignore
    }

    remote.update(doc)
  }

  /**
   * Get a single document
   *
   * @param id The id of the entity
   * @return Future which resolve with the document
   */
  override def get[T: ClassTag](id: ID)(implicit r: Reads[Document]): Future[T] = {
    val p = Promise[T]

    cache.getIfPresent(id) match {
      case Some(x) => p.success(x.asInstanceOf[T])
      case None => {
        remote.get(id).map { result =>
          val document = result.asInstanceOf[Document]
          cache.put(document.id, document)
          p.success(result)
        }.recover {
          case e: Exception => p.failure(e)
        }
      }
    }
    p.future
  }

  /**
   * Delete a document
   *
   * @param id The id of the document to delete
   * @return Future which resolve after deletion
   */
  override def delete(id: ID): Future[Unit] = {
    cache.invalidate(id)
    remote.delete(id)
  }

  /**
   * Query documents which match the specification
   *
   * @param specification The specification for the query
   * @return Observable to return all entities
   */
  override def query[T: ClassTag](specification: Specification): Observable[T] = remote.query(specification)
}
