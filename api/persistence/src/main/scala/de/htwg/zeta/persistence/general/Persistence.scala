package de.htwg.zeta.persistence.general

import java.util.UUID

import scala.concurrent.Future

import models.Identifiable

/** Interface for the Persistence layer.
 *
 * @tparam V type of the document
 */
trait Persistence[V <: Identifiable] { // scalastyle:ignore

  /** The name of the document-type.
   *
   * @param m manifest
   * @return name
   */
  final def name(implicit m: Manifest[V]): String = {
    m.runtimeClass.getSimpleName
  }

  /** Create a new document.
   *
   * @param doc the document to save
   * @return Future, which can fail
   */
  def create(doc: V): Future[Unit]

  /** Get a single document.
   *
   * @param id The id of the entity
   * @return Future which resolve with the document and can fail
   */
  def read(id: UUID): Future[V]

  /** Update a document.
   *
   * @param doc The document to update
   * @return Future, which can fail
   */
  def update(doc: V): Future[Unit]

  /** Delete a document.
   *
   * @param id The id of the document to delete
   * @return Future, which can fail
   */
  def delete(id: UUID): Future[Unit]

  /** Get the id's of all documents.
   *
   * @return Future containing all id's of the document type, can fail
   */
  def readAllIds: Future[Seq[UUID]]

}
