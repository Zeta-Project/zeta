package models.file.http

import models.document.Document
import models.file._
import play.api.libs.ws.{ WSClient, WSRequest }
import rx.lang.scala.Observable

import scala.concurrent.{ Future, Promise }

object HttpRepository {
  def apply(session: String)(implicit client: WSClient): HttpRepository = new HttpRepository(session)
}

/**
 * Http Repository to query the Couchbase Sync Gateway
 */
class HttpRepository(session: String)(implicit client: WSClient) extends Repository {
  import scala.concurrent.ExecutionContext.Implicits.global

  def request(address: String): WSRequest = client.url(address).withHeaders("Cookie" -> s"SyncGatewaySession=${session};")

  /**
   * Create a new file or update an existing
   *
   * @param doc The document to which the file is mapped
   * @param file The File to save
   * @return Future which resolve after create
   */
  def create(doc: Document, file: File): Future[Unit] = {
    val p = Promise[Unit]

    val address = s"http://database:4984/db/${doc.id}/${file.name}"

    request(address)
      .withQueryString("rev" -> doc._rev)
      .put(file.content).map { response =>
        if (response.status == 201) {
          p.success()
        } else {
          println(response.statusText)
          println(response.status)
          p.failure(new Exception(s"The file '${file.name}' could not be saved to the document ${doc}'"))
        }
      }.recover {
        case e: Exception => p.failure(e)
      }
    p.future
  }

  /**
   * Update a file
   *
   * @param doc The document to which the file is mapped
   * @param file The File to update
   * @return Future which resolve after update
   */
  def update(doc: Document, file: File): Future[Unit] = ???

  /**
   * Get a single file
   *
   * @param doc   The id of the document to which the file is mapped
   * @param filename The name of the file
   * @return Future which resolve with the document
   */
  def get(doc: Document, filename: String): Future[File] = {
    val p = Promise[File]

    val address = s"http://database:4984/db/${doc.id}/${filename}"

    request(address)
      .get().map { response =>
        if (response.status == 200) {
          val content = response.body
          p.success(File(filename, content))
        } else {
          p.failure(new Exception(s"The file '${filename}' was not found in the document '${doc.id}'"))
        }
      }.recover {
        case e: Exception => p.failure(e)
      }
    p.future
  }

  /**
   * Delete a file
   *
   * @param file The file which to delete
   * @return Future which resolve after deletion
   */
  def delete(doc: Document, file: File): Future[Unit] = ???

  /**
   * Query files which match the specification
   *
   * @param specification The specification for the query
   * @return Observable to return all entities
   */
  def query(specification: Specification): Observable[File] = ???
}
