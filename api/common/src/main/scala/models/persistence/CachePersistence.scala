package models.persistence

import scala.collection.concurrent.TrieMap

import models.document.Document

/** Cache implementation of Persistence.
 *
 * @tparam T type of the document
 */
class CachePersistence[T <: Document] extends Persistence[T] { // scalastyle:ignore

  private val cache: TrieMap[String, T] = TrieMap.empty[String, T]

  /** Create a new document
   *
   * @param doc The Document to save
   */
  @throws[IllegalArgumentException]
  override def create(doc: T): Unit = {
    if (cache.putIfAbsent(doc.id(), doc).isDefined) {
      throw new IllegalArgumentException("cant't create the document, a document with same id already exists")
    }
  }

  /** Update a document
   *
   * @param doc The Document to update
   */
  @throws[IllegalArgumentException]
  override def update(doc: T): Unit = {
    if (cache.replace(doc.id(), doc).isEmpty) {
      throw new IllegalArgumentException("can't update the document, a document with the id doesn't exist")
    }
  }

  /** Delete a document
   *
   * @param id The id of the document to delete
   */
  @throws[IllegalArgumentException]
  override def delete(id: String): Unit = {
    if (cache.remove(id).isEmpty) {
      throw new IllegalArgumentException("can't delete the document, a document with the id doesn't exist")
    }
  }

  /** Get a single document
   *
   * @param id The id of the entity
   * @return Future which resolve with the document
   */
  @throws[IllegalArgumentException]
  override def read(id: String): T = {
    cache.getOrElse(id, {
      throw new IllegalArgumentException("can't read the document, a document with the id doesn't exist")
    })
  }

  /** Get the id's of all documents.
   *
   * @return all id's of the document type
   */
  override def readAllIds: Seq[String] = {
    cache.keys.toSeq
  }

}
