package de.htwg.zeta.server.controller

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGraphicalDslInstanceRepository
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result

class ModelController @Inject()(
    implicit mat: Materializer,
    system: ActorSystem,
    silhouette: Silhouette[ZetaEnv],
    modelEntityRepo: AccessRestrictedGraphicalDslInstanceRepository
) extends Controller {

  def modelEditor(modelId: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    modelEntityRepo.restrictedTo(request.identity.id).read(modelId).map { model =>
      Ok(views.html.model.ModelGraphicalEditor(model, request.identity.user))
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

}
