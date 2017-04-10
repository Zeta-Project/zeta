package models.document

import play.api.libs.json.Reads
import play.api.libs.json.Writes
import rx.lang.scala.Observable

import scala.concurrent.Future
import scala.reflect.ClassTag

trait Repository {
  type ID = String

  /**
   * Create a new document
   * @param doc The Document to save
   * @return Future which resolve with the created document
   */
  def create[T: ClassTag](doc: Document)(implicit w: Writes[Document]): Future[T]

  /**
   * Update a document
   * @param doc The Document to update
   * @return Future which resolve with the updated document
   */
  def update[T: ClassTag](doc: Document)(implicit w: Writes[Document]): Future[T]

  /**
   * Delete a document
   * @param id The id of the document to delete
   * @return Future which resolve after deletion
   */
  def delete(id: ID): Future[Unit]

  /**
   * Get a single document
   * @param id The id of the entity
   * @return Future which resolve with the document
   */
  def get[T: ClassTag](id: ID)(implicit r: Reads[Document]): Future[T]

  /**
   * Query documents which match the specification
   * @param specification The specification for the query
   * @return Observable to return all entities
   */
  def query[T: ClassTag](specification: Specification): Observable[T]
}
