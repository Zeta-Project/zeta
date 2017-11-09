package de.htwg.zeta.server.controller

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import akka.actor.ActorRef
import akka.actor.Props
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedEntityPersistence
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedMetaModelEntityRepository
import de.htwg.zeta.server.model.metaModel.MetaModelWsActor
import de.htwg.zeta.server.model.metaModel.MetaModelWsMediatorContainer
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.libs.json.JsValue
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result
import play.api.mvc.WebSocket.MessageFlowTransformer


class MetaModelController @Inject()(
    metaModelEntityRepo: AccessRestrictedMetaModelEntityRepository,
    mediator: MetaModelWsMediatorContainer
) extends Controller {

  def metaModelEditor(metaModelId: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    metaModelEntityRepo.restrictedTo(request.identity.id).read(metaModelId).map { metaModelEntity =>
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
