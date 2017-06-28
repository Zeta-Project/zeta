package de.htwg.zeta.server.controller

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.ActorRef
import akka.actor.Props
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.persistence.Persistence.restrictedAccessRepository
import de.htwg.zeta.server.model.metaModel.MetaModelWsActor
import de.htwg.zeta.server.model.metaModel.MetaModelWsMediatorContainer
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.libs.json.JsValue
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result
import play.api.mvc.WebSocket.MessageFlowTransformer

/**
 */
class MetaModelController @Inject()(mediator: MetaModelWsMediatorContainer) extends Controller {

  def metaModelEditor(metaModelId: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    restrictedAccessRepository(request.identity.id).metaModelEntity.read(metaModelId).map { metaModelEntity =>
      Ok(views.html.metamodel.MetaModelGraphicalEditor(Some(request.identity), metaModelId, metaModelEntity))
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  def metaModelSocket(metaModelUuid: UUID)
    (securedRequest: SecuredRequest[ZetaEnv, AnyContent], out: ActorRef): (Props, MessageFlowTransformer[JsValue, JsValue]) = {
    (MetaModelWsActor.props(out, metaModelUuid, mediator), MessageFlowTransformer.jsonMessageFlowTransformer)
  }

}
