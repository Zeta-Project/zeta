package de.htwg.zeta.server.controller

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import akka.actor.ActorRef
import akka.actor.Props
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGdslProjectRepository
import de.htwg.zeta.server.model.metaModel.MetaModelWsActor
import de.htwg.zeta.server.model.metaModel.MetaModelWsMediatorContainer
import de.htwg.zeta.server.silhouette.ZetaEnv
import play.api.libs.json.JsValue
import play.api.mvc.AnyContent
import play.api.mvc.InjectedController
import play.api.mvc.Result
import play.api.mvc.WebSocket.MessageFlowTransformer


class MetaModelController @Inject()(
    metaModelEntityRepo: AccessRestrictedGdslProjectRepository,
    mediator: MetaModelWsMediatorContainer,
    implicit val ec: ExecutionContext
) extends InjectedController {

  def metaModelEditor(metaModelId: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    metaModelEntityRepo.restrictedTo(request.identity.id).read(metaModelId).map { metaModelEntity =>
      Ok(views.html.metamodel.MetaModelGraphicalEditor(Some(request.identity.user), metaModelId, metaModelEntity))
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  def metaModelSocket(metaModelUuid: UUID)
    (securedRequest: SecuredRequest[ZetaEnv, AnyContent], out: ActorRef): (Props, MessageFlowTransformer[JsValue, JsValue]) = {
    (MetaModelWsActor.props(out, metaModelUuid, mediator), MessageFlowTransformer.jsonMessageFlowTransformer)
  }

}
