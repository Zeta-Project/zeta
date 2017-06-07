package de.htwg.zeta.persistence.microService

import java.util.UUID

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
import akka.http.scaladsl.model.StatusCodes
import akka.stream.ActorMaterializer
import de.htwg.zeta.persistence.general.Persistence
import models.Entity
import spray.json.pimpAny
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.UuidJsonFormat
import spray.json.DefaultJsonProtocol.seqFormat
import spray.json.DefaultJsonProtocol.StringJsonFormat
import spray.json.RootJsonFormat



/** MicroService-Client Implementation of Persistence.
 *
 * @param address      ip-address
 * @param port         port
 * @param format       Spray-Json Protocol
 * @param system       ActorSystem
 * @param materializer ActorMaterializer
 * @tparam T type of the document
 */
class PersistenceClient[T <: Entity](address: String, port: Int) // scalastyle:ignore
  (implicit format: RootJsonFormat[T], system: ActorSystem, materializer: ActorMaterializer, manifest: Manifest[T]) extends Persistence[T] {

  private val http = Http()
  private val uri = s"http://$address:$port/$entityTypeName" // scalastyle:ignore

  /** Create a new document.
   *
   * @param doc the document to save
   * @return Future, which can fail
   */
  override def create(doc: T): Future[T] = {
    http.singleRequest(
      HttpRequest(
        method = PUT,
        uri = uri,
        entity = HttpEntity(`application/json`, doc.toJson.toString)
      )
    ).flatMap { response =>
      response.status match {
        case StatusCodes.OK => Unmarshal(response.entity).to[T]
        case _ => Future.failed(new IllegalStateException(response.status.toString))
      }
    }
  }

  /** Get a single document.
   *
   * @param id The id of the entity
   * @return Future which resolve with the document and can fail
   */
  override def read(id: UUID): Future[T] = {
    http.singleRequest(
      HttpRequest(
        method = GET,
        uri = s"$uri/id/$id"
      )
    ).flatMap { response =>
      response.status match {
        case StatusCodes.OK => Unmarshal(response.entity).to[T]
        case _ => Future.failed(new IllegalStateException(response.status.toString))
      }
    }
  }


  /** Delete a document.
   *
   * @param id The id of the document to delete
   * @return Future, which can fail
   */
  override def delete(id: UUID): Future[Unit] = {
    http.singleRequest(HttpRequest(
      method = DELETE,
      uri = s"$uri/id/$id"
    )).flatMap { response =>
      response.status match {
        case StatusCodes.OK => Future.successful(())
        case _ => Future.failed(new IllegalStateException(response.status.toString))
      }
    }
  }

  /** Get the id's of all documents.
   *
   * @return Future containing all id's of the document type, can fail
   */
  override def readAllIds(): Future[Set[UUID]] = {
    http.singleRequest(
      HttpRequest(
        method = GET,
        uri = s"$uri/all"
      )
    ).flatMap { response =>
      Unmarshal(response.entity).to[Seq[UUID]].map(_.toSet)
    }
  }

  /** Update a document.
   *
   * @param doc The document to update
   * @return Future, which can fail
   */
  override def update(doc: T): Future[T] = {
    http.singleRequest(
      HttpRequest(
        method = POST,
        uri = uri,
        entity = HttpEntity(`application/json`, doc.toJson.toString)
      )
    ).flatMap { response =>
      response.status match {
        case StatusCodes.OK => Unmarshal(response.entity).to[T]
        case _ => Future.failed(new IllegalStateException(response.status.toString))
      }
    }
  }

}