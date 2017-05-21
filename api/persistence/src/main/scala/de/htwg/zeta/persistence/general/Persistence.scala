package de.htwg.zeta.persistence.general

import scala.concurrent.Future

import models.document.Document

/** Interface for the Persistence layer.
 *
 * @tparam T type of the document
 */
trait Persistence[T <: Document] { // scalastyle:ignore

  /** The name of the document-type.
   *
   * @param m manifest
   * @return name
   */
  final def name(implicit m: Manifest[T]): String = {
    m.runtimeClass.getSimpleName
  }

  /** Create a new document.
   *
   * @param doc the document to save
   * @return Future, which can fail
   */
  def create(doc: T): Future[Unit]

  /** Get a single document.
   *
   * @param id The id of the entity
   * @return Future which resolve with the document and can fail
   */
  def read(id: String): Future[T]

  /** Update a document.
   *
   * @param doc The document to update
   * @return Future, which can fail
   */
  def update(doc: T): Future[Unit]

  /** Delete a document.
   *
   * @param id The id of the document to delete
   * @return Future, which can fail
   */
  def delete(id: String): Future[Unit]

  /** Get the id's of all documents.
   *
   * @return Future containing all id's of the document type, can fail
   */
  def readAllIds: Future[Seq[String]]

}
