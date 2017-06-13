package de.htwg.zeta.persistence.microService

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.sprayJsonMarshaller
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.sprayJsonUnmarshaller
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.sprayJsValueMarshaller
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._enhanceRouteWithConcatenation
import akka.http.scaladsl.server.Directives._segmentStringToPathMatcher
import akka.http.scaladsl.server.Directives.as
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.Directives.delete
import akka.http.scaladsl.server.Directives.entity
import akka.http.scaladsl.server.Directives.get
import akka.http.scaladsl.server.Directives.onComplete
import akka.http.scaladsl.server.Directives.onSuccess
import akka.http.scaladsl.server.Directives.path
import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Directives.post
import akka.http.scaladsl.server.Directives.put
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.StandardRoute
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.HttpEntity
import akka.stream.ActorMaterializer
import de.htwg.zeta.persistence.general.EntityPersistence
import de.htwg.zeta.persistence.general.Repository
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.bondedTaskFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.eventDrivenTaskFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.filterFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.filterImageFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.generatorFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.generatorImageFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.logFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.settingsFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.UuidJsonFormat
import grizzled.slf4j.Logging
import models.entity.Entity
import spray.json.RootJsonFormat
import spray.json.pimpAny
import spray.json.DefaultJsonProtocol.seqFormat
import spray.json.DefaultJsonProtocol.StringJsonFormat


/**
 * A Micro-Service for the persistence-layer.
 */
object PersistenceServer extends Logging {

  private implicit val system = ActorSystem("persistenceServer")
  private implicit val materializer = ActorMaterializer()

  /** Start a new Persistence Server.
   *
   * @param address IP-Address
   * @param port    port
   * @param service underlaying persistence
   * @return Future, which can fail
   */
  def start(address: String, port: Int, service: Repository): Future[Unit] = {
    val route: Route =
      persistenceRoutes(service.bondTasks) ~
      persistenceRoutes(service.eventDrivenTasks) ~
      persistenceRoutes(service.filters) ~
      persistenceRoutes(service.filterImages) ~
      persistenceRoutes(service.generators) ~
      persistenceRoutes(service.generatorImages) ~
      persistenceRoutes(service.logs) ~
      persistenceRoutes(service.settings)


    Http().bindAndHandle(route, address, port).flatMap { _ =>
      info(s"PersistenceServer running at http://$address:$port/")
      Future.successful(())
    }
  }

  private def persistenceRoutes[T <: Entity](service: EntityPersistence[T])(implicit jsonFormat: RootJsonFormat[T], manifest: Manifest[T]): Route = {
    pathPrefix(service.entityTypeName / "id" /
      """\w+""".r) { id =>
      get {
        onComplete(service.read(UUID.fromString(id))) {
          case Success(doc) => complete((
            StatusCodes.OK,
            HttpEntity(`application/json`, doc.toJson.toString)
          ))
          case Failure(e) => completeWithError(e, "reading", service.entityTypeName, UUID.fromString(id))
        }
      } ~
        delete {
          onComplete(service.delete(UUID.fromString(id))) {
            case Success(_) => complete(StatusCodes.OK)
            case Failure(e) => completeWithError(e, "deleting", service.entityTypeName, UUID.fromString(id))
          }
        }
    } ~
      pathPrefix(service.entityTypeName) {
        put {
          entity(as[T]) { doc =>
            onComplete(service.create(doc)) {
              case Success(_) => complete(StatusCodes.OK)
              case Failure(e) => completeWithError(e, "creating", service.entityTypeName, doc.id)
            }
          }
        } ~
          post {
            entity(as[T]) { doc =>
              onComplete(service.update(doc.id, _ => doc)) {
                case Success(_) => complete(StatusCodes.OK)
                case Failure(e) => completeWithError(e, "updating", service.entityTypeName, doc.id)
              }
            }
          }
      } ~
      get {
        path(service.entityTypeName / "all") {
          onSuccess(service.readAllIds()) { allIds =>
            complete((
              StatusCodes.OK,
              HttpEntity(`application/json`, allIds.toSeq.toJson.toString)
            ))
          }
        }
      }
  }

  private def completeWithError(e: Throwable, action: String, docType: String, id: UUID): StandardRoute = {
    val msg = s"$action failed (docType: $docType | id: $id)"
    error(s"$msg - ${e.getMessage}")
    complete((StatusCodes.BadRequest, msg))
  }

}
