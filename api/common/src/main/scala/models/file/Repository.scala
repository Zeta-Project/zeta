package models.file

import models.document.Document
import rx.lang.scala.Observable

import scala.concurrent.Future

trait Repository {
  //type Document = String

  /**
   * Create a new file
   *
   * @param doc The document to which the file is mapped
   * @param file The File to save
   * @return Future which resolve after create
   */
  def create(doc: Document, file: File): Future[Unit]

  /**
   * Update a file
   *
   * @param doc The document to which the file is mapped
   * @param file The File to update
   * @return Future which resolve after update
   */
  def update(doc: Document, file: File): Future[Unit]

  /**
   * Delete a file
   *
   * @param doc The document to which the file is mapped
   * @param file The file which to delete
   * @return Future which resolve after deletion
   */
  def delete(doc: Document, file: File): Future[Unit]

  /**
   * Get a single file
   *
   * @param doc The document to which the file is mapped
   * @param name The name of the file
   * @return Future which resolve with the document
   */
  def get(doc: Document, name: String): Future[File]

  /**
   * Query files which match the specification
   *
   * @param specification The specification for the query
   * @return Observable to return all entities
   */
  def query(specification: Specification): Observable[File]
}