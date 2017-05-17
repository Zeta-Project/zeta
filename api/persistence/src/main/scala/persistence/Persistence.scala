package persistence

import models.document.Document

/** Interface for the Persistence layer.
 *
 * @tparam T type of the document
 */
trait Persistence[T <: Document] { // scalastyle:ignore

  /** Create a new document.
   * <p>
   * Throws an exception if the document already exists
   *
   * @param doc The Document to save
   */
  @throws[Exception]
  def create(doc: T): Unit

  /** Get a single document.
   *
   * @param id The id of the entity
   * @return Future which resolve with the document
   */
  @throws[Exception]
  def read(id: String): T

  /** Update a document.
   *
   * @param doc The document to update
   */
  @throws[Exception]
  def update(doc: T): Unit

  /** Delete a document.
   *
   * @param id The id of the document to delete
   */
  @throws[Exception]
  def delete(id: String): Unit

  /** Get the id's of all documents.
   *
   * @return all id's of the document type
   */
  @throws[Exception]
  def readAllIds: Seq[String]

}
