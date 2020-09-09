package de.htwg.zeta.server.controller.webpage

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.format.GraphicalDslInstanceShortInfo
import de.htwg.zeta.common.format.ProjectShortInfo
import de.htwg.zeta.common.format.entity.UserFormat
import de.htwg.zeta.common.format.project.GdslProjectFormat
import de.htwg.zeta.common.models.entity.User
import de.htwg.zeta.common.models.project.GdslProject
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGdslProjectRepository
import de.htwg.zeta.persistence.general.GraphicalDslInstanceRepository
import de.htwg.zeta.server.routing.routes
import de.htwg.zeta.server.silhouette.ZetaEnv
import play.api.i18n.Messages
import play.api.libs.json.JsNull
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.Result
import views.html.helper.CSRF

class WebpageController @Inject()(
    modelEntityRepo: GraphicalDslInstanceRepository,
    metaModelEntityRepo: AccessRestrictedGdslProjectRepository,
    gdslProjectFormat: GdslProjectFormat,
    userFormat: UserFormat,
    ws: WSClient
) extends Controller {


  def index(request: SecuredRequest[ZetaEnv, AnyContent]): Result = {
    Redirect(routes.ScalaRoutes.getOverviewNoArgs())
  }

  /**
   * Return a CSRF Token for further POST requests
   * @param request
   * @param messages
   * @return
   */
  def csrf(request: Request[AnyContent], messages: Messages): Future[Result] = {
    Future.successful(Ok(Json.obj("csrf" -> CSRF.getToken(request).value)))
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

  private def asJson(
      user: Option[User],
      metaModels: Seq[ProjectShortInfo],
      gdslProject: Option[GdslProject],
      modelInstances: Seq[GraphicalDslInstanceShortInfo]) = {
    Json.obj(
      "user" -> unwrapOrNull(user).fold(r => r,l => userFormat.writes(l)),
      "metaModels" -> metaModels,
      "gdslProject" -> unwrapOrNull(gdslProject).fold(r => r,l => gdslProjectFormat.writes(l)),
      "modelInstances" -> modelInstances
    );
  }

  private def unwrapOrNull[A](input: Option[A]): Either[JsValue, A] = input match {
    case Some(value) => Right(value)
    case None => Left(JsNull)
  }

  def diagramsOverviewShortInfo(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    val result = for {
      metaModels <- getMetaModels(request)
    } yield {
      Ok(asJson(Some(request.identity.user),metaModels, None,Seq[GraphicalDslInstanceShortInfo]()))
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
        Ok(asJson(Some(request.identity.user),metaModels, Some(metaModel),Seq[GraphicalDslInstanceShortInfo]()))
      }

      result.recover {
        case e: Exception => BadRequest(e.getMessage)
      }
    }
  }

}

