package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.{ HandlerResult, Silhouette }
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import models.document.{ MetaModelEntity, Repository }
import models.metaModel._
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc.{ AnyContentAsEmpty, Controller, Request, WebSocket }
import utils.auth.{ DefaultEnv, RepositoryFactory }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by mgt on 17.10.15.
 */

class MetaModelController @Inject() (implicit mat: Materializer, system: ActorSystem, repositoryFactory: RepositoryFactory, silhouette: Silhouette[DefaultEnv]) extends Controller {

  def repository[A]()(implicit request: SecuredRequest[DefaultEnv, A]): Repository =
    repositoryFactory.fromSession(request)

  def metaModelEditor(metaModelUuid: String) = silhouette.SecuredAction.async { implicit request =>
    repository.get[MetaModelEntity](metaModelUuid).map { metaModelEntity =>
      // Fix Graph with MetaModelGraphDiff
      //val oldMetaModelEntity = metaModelEntity.get
      //val fixedConcept = MetamodelGraphDiff.fixGraph(oldMetaModelEntity.metaModel)
      //val fixedDefinition = oldMetaModelEntity.metaModel.copy(concept = fixedConcept)
      //val fixedMetaModelEntity = oldMetaModelEntity.copy(metaModel = fixedConcept)

      Ok(views.html.metamodel.MetaModelGraphicalEditor(Some(request.identity), metaModelUuid, metaModelEntity))
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  def metaModelSocket(metaModelUuid: String) = WebSocket.acceptOrResult[JsValue, JsValue] { request =>
    implicit val req = Request(request, AnyContentAsEmpty)
    silhouette.SecuredRequestHandler { securedRequest =>
      Future.successful(HandlerResult(Ok, Some(securedRequest.identity)))
    }.map {
      case HandlerResult(r, Some(user)) => Right(ActorFlow.actorRef(out => MetaModelWsActor.props(out, metaModelUuid)))
      case HandlerResult(r, None) => Left(r)
    }
  }
}
