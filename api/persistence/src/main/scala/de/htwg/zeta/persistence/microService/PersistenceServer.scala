package de.htwg.zeta.persistence.microService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure
import scala.util.Success
import scala.util.Try

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
import akka.http.scaladsl.server.Directives.path
import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Directives.post
import akka.http.scaladsl.server.Directives.put
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.StandardRoute
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import de.htwg.zeta.persistence.dbaccess.Persistence
import de.htwg.zeta.persistence.service.PersistenceService
import grizzled.slf4j.Logging
import models.document.Document
import spray.json.RootJsonFormat
import spray.json.pimpAny
import spray.json.DefaultJsonProtocol.seqFormat
import spray.json.DefaultJsonProtocol.StringJsonFormat

/**
 *  A Micro-Service for the persistence-layer.
 */
object PersistenceServer extends Logging {

  private implicit val system = ActorSystem("persistence")
  private implicit val materializer = ActorMaterializer()

  /** Start a new Persistence Server.
   *
   * @param address IP-Address
   * @param port port
   * @param service underlaying persistence
   */
  def start(address: String, port: Int, service: PersistenceService): Unit = {
    val route: Route =
    persistenceRoutes("passwordInfoEntity", service.passwordInfoEntity)(PersistenceJsonProtocol.passwordInfoEntity) ~
      persistenceRoutes("userEntity", service.userEntity)(PersistenceJsonProtocol.userEntityFormat)

    Http().bindAndHandle(route, address, port) onSuccess {
      case _ => info(s"PersistenceServer running at http://$address:$port/")
    }
  }

  private def persistenceRoutes[T <: Document](name: String, service: Persistence[T])(implicit format: RootJsonFormat[T]): Route = {
    pathPrefix(name / "id" /
      """\w+""".r) { id =>
      get {
        Try(service.read(id)) match {
          case Success(doc) => complete((StatusCodes.OK, doc.toJson))
          case Failure(e) => completeWithError(e, "reading", name, id)
        }
      } ~
        delete {
          Try(service.delete(id)) match {
            case Success(_) => complete(StatusCodes.OK)
            case Failure(e) => completeWithError(e, "deleting", name, id)
          }
        }
    } ~
      pathPrefix(name) {
        put {
          entity(as[T]) { doc =>
            Try(service.create(doc)) match {
              case Success(_) => complete(StatusCodes.OK)
              case Failure(e) => completeWithError(e, "creating", name, doc.id())
            }
          }
        } ~
          post {
            entity(as[T]) { doc =>
              Try(service.update(doc)) match {
                case Success(_) => complete(StatusCodes.OK)
                case Failure(e) => completeWithError(e, "updating", name, doc.id())
              }
            }
          }
      } ~
      get {
        path(name / "all") {
          complete(service.readAllIds)
        }
      }
  }

  private def completeWithError(e: Throwable, action: String, docType: String, id: String): StandardRoute = {
    val msg = s"$action failed (docType: $docType | id: $id)"
    error(s"$msg - ${e.getMessage}")
    complete((StatusCodes.BadRequest, msg))

  }

}
