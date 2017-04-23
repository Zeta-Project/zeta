package models.document

import scala.concurrent.Future

trait Cache {
  /**
   * Invalidate a cache entry
   * @param id The id of the invalid entry
   * @return Future which resolve after invalidation
   */
  def invalidate(id: String): Future[Unit]
}
