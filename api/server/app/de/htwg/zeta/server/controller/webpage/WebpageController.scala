package de.htwg.zeta.server.controller.webpage

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import controllers.routes
import de.htwg.zeta.persistence.Persistence.restrictedAccessRepository
import de.htwg.zeta.server.util.auth.ZetaEnv
import models.modelDefinitions.metaModel.MetaModelShortInfo
import models.modelDefinitions.model.ModelShortInfo
import play.api.libs.ws.WSClient
import play.api.mvc.Controller
import play.api.mvc.AnyContent
import play.api.mvc.Result

class WebpageController @Inject()(ws: WSClient) extends Controller {


  def index(request: SecuredRequest[ZetaEnv, AnyContent]): Result = {
    Redirect(routes.ScalaRoutes.getOverviewNoArgs())
  }

  private def getMetaModels[A](request: SecuredRequest[ZetaEnv, A]): Future[Seq[MetaModelShortInfo]] = {
    val repo = restrictedAccessRepository(request.identity.id).metaModelEntities
    repo.readAllIds.flatMap { ids =>
      Future.sequence(ids.map(repo.read)).map(_.map(entity => {
        MetaModelShortInfo(entity.id, entity.name, entity.links)
      }))
    }
  }

  private def getModels[A](metaModelId: UUID, request: SecuredRequest[ZetaEnv, A]): Future[Seq[ModelShortInfo]] = {
    val repo = restrictedAccessRepository(request.identity.id).modelEntities
    repo.readAllIds.flatMap { ids =>
      Future.sequence(ids.map(repo.read)).map(_.filter(_.metaModelId == metaModelId).map(entity => {
        ModelShortInfo(entity.id, entity.metaModelId, entity.model.name)
      }))
    }
  }

  def diagramsOverviewShortInfo(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    val result = for {
      metaModels <- getMetaModels(request)
    } yield {
      Ok(views.html.webpage.WebpageDiagramsOverview(Some(request.identity), metaModels, None, Seq[ModelShortInfo]()))
    }

    result.recover {
      case e: Exception => BadRequest(e.getMessage)
    }

  }

  def diagramsOverview(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    if (id == null) {
      diagramsOverviewShortInfo(request)
    } else {
      val result: Future[Result] = for {
        metaModels <- getMetaModels(request)
        models <- getModels(id, request)
        metaModel <- restrictedAccessRepository(request.identity.id).metaModelEntities.read(id)
      } yield {
        Ok(views.html.webpage.WebpageDiagramsOverview(Some(request.identity), metaModels, Some(metaModel), models))
      }

      result.recover {
        case e: Exception => BadRequest(e.getMessage)
      }
    }
  }

}

