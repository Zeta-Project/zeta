package de.htwg.zeta.server.controller

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.Future

import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.persistence.Persistence
import de.htwg.zeta.persistence.Persistence.restrictedAccessRepository
import de.htwg.zeta.server.model.metaModel.MetaModelWsActor
import de.htwg.zeta.server.util.auth.ZetaEnv
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
    silhouette: Silhouette[ZetaEnv])
  extends Controller {

  def metaModelEditor(metaModelId: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    restrictedAccessRepository(request.identity.id).metaModelEntities.read(metaModelId).map { metaModelEntity =>
      Ok(views.html.metamodel.MetaModelGraphicalEditor(Some(request.identity), metaModelId, metaModelEntity))
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  def metaModelSocket(metaModelUuid: UUID)
    (securedRequest: SecuredRequest[ZetaEnv, AnyContent], out: ActorRef): (Props, MessageFlowTransformer[JsValue, JsValue]) = {
    (MetaModelWsActor.props(out, metaModelUuid), MessageFlowTransformer.jsonMessageFlowTransformer)
  }

}
