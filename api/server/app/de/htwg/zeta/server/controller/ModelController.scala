package de.htwg.zeta.server.controller

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.persistence.Persistence.restrictedAccessRepository
import de.htwg.zeta.server.model.model.ModelWsActor
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.mvc.Controller
import play.api.mvc.AnyContent
import play.api.mvc.Result

class ModelController @Inject()(
    implicit mat: Materializer,
    system: ActorSystem,
    silhouette: Silhouette[ZetaEnv])
  extends Controller {

  def modelEditor(modelId: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    restrictedAccessRepository(request.identity.id).modelEntity.read(modelId).map { model =>
      Ok(views.html.model.ModelGraphicalEditor(model.metaModelId, model.id, Some(request.identity), model))
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  def vrModelEditor(modelUuid: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    restrictedAccessRepository(request.identity.id).modelEntity.read(modelUuid).map { model =>
      Ok(views.html.VrEditor(model.metaModelId))
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  def modelSocket(instanceId: UUID, graphType: String)(securedRequest: SecuredRequest[ZetaEnv, AnyContent], out: ActorRef): Props = {
    ModelWsActor.props(out, instanceId, graphType)
  }
}
