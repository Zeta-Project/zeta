package de.htwg.zeta.server.controller.webpage

import java.util.UUID

import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import controllers.routes
import de.htwg.zeta.common.format.GraphicalDslInstanceShortInfo
import de.htwg.zeta.common.format.ProjectShortInfo
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGdslProjectRepository
import de.htwg.zeta.persistence.general.GraphicalDslInstanceRepository
import de.htwg.zeta.server.silhouette.ZetaEnv
import play.api.libs.ws.WSClient
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result

class WebpageController @Inject()(
    modelEntityRepo: GraphicalDslInstanceRepository,
    metaModelEntityRepo: AccessRestrictedGdslProjectRepository,
    ws: WSClient
) extends Controller {


  def index(request: SecuredRequest[ZetaEnv, AnyContent]): Result = {
    Redirect(routes.ScalaRoutes.getOverviewNoArgs())
  }

  private def getMetaModels[A](request: SecuredRequest[ZetaEnv, A]): Future[Seq[ProjectShortInfo]] = {
    val repo = metaModelEntityRepo.restrictedTo(request.identity.id)
    repo.readAllIds().flatMap { ids =>
      Future.sequence(ids.toList.map(repo.read)).map(_.map(entity => {
        ProjectShortInfo(entity.id, entity.name)
      }))
    }
  }

  private def getModels[A](metaModelId: UUID, request: SecuredRequest[ZetaEnv, A]): Future[Seq[GraphicalDslInstanceShortInfo]] = {
    val repo = modelEntityRepo
    repo.readAllIds().flatMap { ids =>
      Future.sequence(ids.toList.map(repo.read)).map(_.filter(_.graphicalDslId == metaModelId).map(entity => {
        GraphicalDslInstanceShortInfo(entity.id, entity.graphicalDslId, entity.name)
      }))
    }
  }

  def diagramsOverviewShortInfo(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    val result = for {
      metaModels <- getMetaModels(request)
    } yield {
      Ok(views.html.webpage.WebpageDiagramsOverview(Some(request.identity.user), metaModels, None, Seq[GraphicalDslInstanceShortInfo]()))
    }

    result.recover {
      case e: Exception => BadRequest(e.getMessage)
    }

  }

  def diagramsOverview(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    if (id == null) { // scalastyle:ignore null
      diagramsOverviewShortInfo(request)
    } else {
      val result: Future[Result] = for {
        metaModels <- getMetaModels(request)
        models <- getModels(id, request)
        metaModel <- metaModelEntityRepo.restrictedTo(request.identity.id).read(id)
      } yield {
        Ok(views.html.webpage.WebpageDiagramsOverview(Some(request.identity.user), metaModels, Some(metaModel), models))
      }

      result.recover {
        case e: Exception => BadRequest(e.getMessage)
      }
    }
  }

}

