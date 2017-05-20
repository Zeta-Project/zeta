package de.htwg.zeta.persistence.microService

import scala.concurrent.Future

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.HttpMethods.DELETE
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.HttpMethods.POST
import akka.http.scaladsl.model.HttpMethods.PUT
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import models.document.Document
import spray.json.pimpAny


/** MicroService-Client Implementation of Persistence.
 *
 * @tparam T type of the document
 */
class PersistenceClient[T <: Document](address: String, port: Int, docType: String) { // scalastyle:ignore

  private val uri = s"http://$address:$port/$docType" // scalastyle:ignore

  /** Create a new document.
   *
   * Throws an exception if the document already exists
   *
   * @param doc The Document to save
   * @return Unit-Future
   */
  def create(doc: T): Future[Unit] = {
    Http().singleRequest(
      HttpRequest(
        method = PUT,
        uri = uri,
        entity = HttpEntity(
          `application/json`,
          doc.toJson.toString
        )
      )
    ).flatMap { _ =>
      Future.successful()
    }
  }

  /** Get a single document.
   *
   * @param id The id of the entity
   * @return Future which resolve with the document
   */
  def read(id: String): Future[T] = {
    Http().singleRequest(
      HttpRequest(
        method = GET,
        uri = s"$uri/id/$id"
      )
    ).flatMap { response =>
      Unmarshal(response.entity).to[T]
    }
  }

  /** Update a document.
   *
   * @param doc The document to update
   * @return Unit-Future
   */
  def update(doc: T): Future[Unit] = {
    Http().singleRequest(
      HttpRequest(
        method = POST,
        uri = uri,
        entity = HttpEntity(
          `application/json`,
          doc.toJson.toString
        )
      )
    ).flatMap { _ =>
      Future.successful()
    }
  }

  /** Delete a document.
   *
   * @param id The id of the document to delete
   * @return Unit-Future
   */
  def delete(id: String): Future[Unit] = {
    Http().singleRequest(
      HttpRequest(
        method = DELETE,
        uri = s"$uri/id/$id"
      )
    ).flatMap { _ =>
      Future.successful()
    }
  }

  /** Get the id's of all documents.
   *
   * @return all id's of the document type
   */
  def readAllIds: Future[Seq[String]] = {
    Http().singleRequest(
      HttpRequest(
        method = POST,
        uri = s"$uri/all"
      )
    ).flatMap { response =>
      Unmarshal(response.entity).to[Seq[String]]
    }
  }

}
