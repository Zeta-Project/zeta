package de.htwg.zeta.server.controller.restApi.v2

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.format.project.TaskResultFormat
import de.htwg.zeta.common.format.project.gdsl.DiagramsFormat
import de.htwg.zeta.common.format.project.gdsl.StylesFormat
import de.htwg.zeta.common.format.project.gdsl.shape.ShapeFormat
import de.htwg.zeta.common.models.project.GdslProject
import de.htwg.zeta.common.models.project.TaskResult
import de.htwg.zeta.common.models.project.gdsl.GraphicalDsl
import de.htwg.zeta.parser.GraphicalDSLParser
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGdslProjectRepository
import de.htwg.zeta.server.silhouette.ZetaEnv
import grizzled.slf4j.Logging
import play.api.libs.json.JsObject
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result

class GraphicalDslRestApi @Inject()(
    gdslProjectRepo: AccessRestrictedGdslProjectRepository,
    graphicalDslParser: GraphicalDSLParser,
    taskResultFormat: TaskResultFormat,
    stylesFormat: StylesFormat,
    diagramsFormat: DiagramsFormat,
    shapeFormat: ShapeFormat
) extends Controller with Logging {

  def triggerParse(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] =
    protectedRead(id, request, graphicalDsl => {
      graphicalDslParser.parse(
        graphicalDsl.concept,
        graphicalDsl.style,
        graphicalDsl.shape,
        graphicalDsl.diagram
      ).fold[Result](
        errorResult => Ok(taskResultFormat.writes(TaskResult.error(errorResult.errorDsl, errorResult.errors))),
        _ => Ok(taskResultFormat.writes(TaskResult.success()))
      )
    })

  def getStyle(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    parseAndSerialize(id, request, g => stylesFormat.writes(g.styles))
  }

  def getShape(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    parseAndSerialize(id, request, g => shapeFormat.writes(g.shape))
  }

  def getDiagram(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    parseAndSerialize(id, request, g => diagramsFormat.writes(g.diagrams))
  }

  private def parseAndSerialize[A](id: UUID, request: SecuredRequest[ZetaEnv, A], serialize: GraphicalDsl => JsObject): Future[Result] = {
    protectedRead(id, request, graphicalDsl => {
      graphicalDslParser.parse(
        graphicalDsl.concept,
        graphicalDsl.style,
        graphicalDsl.shape,
        graphicalDsl.diagram
      ).fold[Result](
        errorResult => InternalServerError(taskResultFormat.writes(TaskResult.error(errorResult.errorDsl, errorResult.errors))),
        successResult => Ok(serialize(successResult))
      )
    })
  }

  /** A helper method for less verbose reads from the database */
  private def protectedRead[A](id: UUID, request: SecuredRequest[ZetaEnv, A], trans: GdslProject => Result): Future[Result] = {
    gdslProjectRepo.restrictedTo(request.identity.id).read(id).map { graphicalDsl =>
      trans(graphicalDsl)
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

}
