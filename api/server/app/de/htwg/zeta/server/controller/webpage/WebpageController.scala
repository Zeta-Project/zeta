package de.htwg.zeta.server.controller.webpage

import javax.inject.Inject

import scala.concurrent.Promise
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import controllers.routes
import de.htwg.zeta.server.util.auth.ZetaEnv
import models.document.AllMetaModels
import models.document.AllModels
import models.document.MetaModelEntity
import models.document.ModelEntity
import models.document.Repository
import models.document.http.HttpRepository
import models.modelDefinitions.metaModel.MetaModelShortInfo
import models.modelDefinitions.model.ModelShortInfo
import play.api.libs.ws.WSClient
import play.api.mvc.Controller
import play.api.mvc.AnyContent
import play.api.mvc.Result
import rx.lang.scala.Notification.OnError
import rx.lang.scala.Notification.OnNext

class WebpageController @Inject()(ws: WSClient) extends Controller {

  private def repository[A](request: SecuredRequest[ZetaEnv, A]): Repository =
    new HttpRepository(request.cookies.get("SyncGatewaySession").get.value)(ws)

  def index(request: SecuredRequest[ZetaEnv, AnyContent]): Result = {
    Redirect(routes.ScalaRoutes.diagramsOverviewShortInfo())
  }

  private def getMetaModels[A](request: SecuredRequest[ZetaEnv, A]): Future[Seq[MetaModelShortInfo]] = {
    val p = Promise[Seq[MetaModelShortInfo]]

    repository(request).query[MetaModelEntity](AllMetaModels())
      .map { entity =>
        MetaModelShortInfo(entity.id(), entity.name, entity.links)
      }
      .toSeq.materialize.subscribe(n => n match {
      case OnError(err) => p.failure(err)
      case OnNext(list) => p.success(list)
    })

    p.future
  }

  private def getModels[A](metaModel: String, request: SecuredRequest[ZetaEnv, A]): Future[Seq[ModelShortInfo]] = {
    val p = Promise[Seq[ModelShortInfo]]

    if (metaModel != null) {
      repository(request).query[ModelEntity](AllModels())
        .filter { entity =>
          entity.metaModelId == metaModel
        }
        .map { entity =>
          ModelShortInfo(entity.id(), entity.metaModelId, entity.model.name)
        }
        .toSeq.materialize.subscribe(n => n match {
        case OnError(err) => p.failure(err)
        case OnNext(list) => p.success(list)
      })
    } else {
      p.success(Seq())
    }
    p.future
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

  def diagramsOverview(uuid: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    if (uuid == null) {
      diagramsOverviewShortInfo(request)
    } else {
      val result: Future[Result] = for {
        metaModels <- getMetaModels(request)
        models <- getModels(uuid, request)
        metaModel <- repository(request).get[MetaModelEntity](uuid)
      } yield {
        Ok(views.html.webpage.WebpageDiagramsOverview(Some(request.identity), metaModels, Some(metaModel), models))
      }

      result.recover {
        case e: Exception => BadRequest(e.getMessage)
      }
    }
  }

}

