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
import de.htwg.zeta.server.model.model.ModelWsActor
import de.htwg.zeta.server.util.auth.ZetaEnv
import de.htwg.zeta.server.util.auth.RepositoryFactory
import models.document.ModelEntity
import models.document.Repository
import play.api.mvc.Controller
import play.api.mvc.AnyContent
import play.api.mvc.Result

class ModelController @Inject()(
    implicit mat: Materializer,
    system: ActorSystem,
    repositoryFactory: RepositoryFactory,
    silhouette: Silhouette[ZetaEnv])
  extends Controller {

  private def repository[A](request: SecuredRequest[ZetaEnv, A]): Repository =
    repositoryFactory.fromSession(request)

  def modelEditor(metaModelUuid: String, modelUuid: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    repository(request).get[ModelEntity](modelUuid).map { model =>
      Ok(views.html.model.ModelGraphicalEditor(model.metaModelId, modelUuid, Some(request.identity), model))
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  def vrModelEditor(metaModelUuid: String, modelUuid: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    repository(request).get[ModelEntity](modelUuid).map { _ =>
      Ok(views.html.VrEditor(metaModelUuid))
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  def modelValidator(request: SecuredRequest[ZetaEnv, AnyContent]): Result = {
    Ok(views.html.model.ModelValidator(Some(request.identity)))
  }

  def modelSocket(instanceId: String, graphType: String)(securedRequest: SecuredRequest[ZetaEnv, AnyContent], out: ActorRef): Props = {
    ModelWsActor.props(out, instanceId, graphType)
  }
}
