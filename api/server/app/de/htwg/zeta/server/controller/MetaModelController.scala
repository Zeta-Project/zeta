package de.htwg.zeta.server.controller

import javax.inject.Inject

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.server.model.metaModel.MetaModelWsActor
import de.htwg.zeta.server.util.auth.ZetaEnv
import de.htwg.zeta.server.util.auth.RepositoryFactory
import models.document.MetaModelEntity
import models.document.Repository
import play.api.libs.json.JsValue
import play.api.mvc.Controller
import play.api.mvc.AnyContent
import play.api.mvc.Result
import play.api.mvc.WebSocket.MessageFlowTransformer

/**
 */
class MetaModelController @Inject()(
    implicit mat: Materializer,
    system: ActorSystem,
    repositoryFactory: RepositoryFactory,
    silhouette: Silhouette[ZetaEnv])
  extends Controller {

  private def repository[A](request: SecuredRequest[ZetaEnv, A]): Repository =
    repositoryFactory.fromSession(request)

  def metaModelEditor(metaModelUuid: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    repository(request).get[MetaModelEntity](metaModelUuid).map { metaModelEntity =>
      Ok(views.html.metamodel.MetaModelGraphicalEditor(Some(request.identity), metaModelUuid, metaModelEntity))
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  def metaModelSocket(metaModelUuid: String)
    (securedRequest: SecuredRequest[ZetaEnv, AnyContent], out: ActorRef): (Props, MessageFlowTransformer[JsValue, JsValue]) = {
    (MetaModelWsActor.props(out, metaModelUuid), MessageFlowTransformer.jsonMessageFlowTransformer)
  }
}
