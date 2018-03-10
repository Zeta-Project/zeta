package de.htwg.zeta.server.controller.restApi.v2

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.format.metaModel.ClassFormat
import de.htwg.zeta.common.format.metaModel.ConceptFormat
import de.htwg.zeta.common.format.metaModel.GraphicalDslFormat
import de.htwg.zeta.common.format.metaModel.ReferenceFormat
import de.htwg.zeta.common.models.entity.GraphicalDsl
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGraphicalDslRepository
import de.htwg.zeta.server.silhouette.ZetaEnv
import grizzled.slf4j.Logging
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result

class GraphicalDslRestApi @Inject()(
    graphicalDslRepo: AccessRestrictedGraphicalDslRepository,
    conceptFormat: ConceptFormat,
    graphicalDslFormat: GraphicalDslFormat,
    classFormat: ClassFormat,
    referenceFormat: ReferenceFormat
) extends Controller with Logging {

  def getStyle(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, graphicalDsl => Ok(graphicalDsl.style))
  }

  /** returns shape definition */
  def getShape(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, graphicalDsl => Ok(graphicalDsl.shape))
  }

  /** returns diagram definition */
  def getDiagram(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, graphicalDsl => Ok(graphicalDsl.diagram))
  }

  /** A helper method for less verbose reads from the database */
  private def protectedRead[A](id: UUID, request: SecuredRequest[ZetaEnv, A], trans: GraphicalDsl => Result): Future[Result] = {
    graphicalDslRepo.restrictedTo(request.identity.id).read(id).map { graphicalDsl =>
      trans(graphicalDsl)
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

}
