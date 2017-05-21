package de.htwg.zeta.persistence.microService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.sprayJsonUnmarshaller
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.HttpMethods.DELETE
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.HttpMethods.POST
import akka.http.scaladsl.model.HttpMethods.PUT
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import de.htwg.zeta.persistence.dbaccess.Persistence
import models.document.Document
import spray.json.pimpAny
import spray.json.DefaultJsonProtocol.seqFormat
import spray.json.DefaultJsonProtocol.StringJsonFormat
import spray.json.RootJsonFormat

/** MicroService-Client Implementation of Persistence.
 *
 * @tparam T type of the document
 */
class PersistenceClient[T <: Document](address: String, port: Int, docType: String)(implicit format: RootJsonFormat[T]) extends Persistence[T] { // scalastyle:ignore

  private implicit val system = ActorSystem()
  private implicit val materializer = ActorMaterializer()
  private val http = Http()
  private val uri = s"http://$address:$port/$docType" // scalastyle:ignore


  /** Create a new document.
   *
   * @param doc the document to save
   * @return Future, which can fail
   */
  override def create(doc: T): Future[Unit] = {
    http.singleRequest(
      HttpRequest(
        method = PUT,
        uri = uri,
        entity = HttpEntity(
          `application/json`,
          doc.toJson.toString
        )
      )
    ).flatMap { _ =>
      Future.successful(Unit)
    }
  }

  /** Get a single document.
   *
   * @param id The id of the entity
   * @return Future which resolve with the document and can fail
   */
  override def read(id: String): Future[T] = {
    http.singleRequest(
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
   * @return Future, which can fail
   */
  override def update(doc: T): Future[Unit] = {
    http.singleRequest(
      HttpRequest(
        method = POST,
        uri = uri,
        entity = HttpEntity(
          `application/json`,
          doc.toJson.toString
        )
      )
    ).flatMap { _ =>
      Future.successful(Unit)
    }
  }

  /** Delete a document.
   *
   * @param id The id of the document to delete
   * @return Future, which can fail
   */
  override def delete(id: String): Future[Unit] = {
    http.singleRequest(
      HttpRequest(
        method = DELETE,
        uri = s"$uri/id/$id"
      )
    ).flatMap { _ =>
      Future.successful(Unit)
    }
  }

  /** Get the id's of all documents.
   *
   * @return Future containing all id's of the document type, can fail
   */
  override def readAllIds: Future[Seq[String]] = {
    http.singleRequest(
      HttpRequest(
        method = POST,
        uri = s"$uri/all"
      )
    ).flatMap { response =>
      Unmarshal(response.entity).to[Seq[String]]
    }
  }

}
