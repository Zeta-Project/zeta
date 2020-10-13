package de.htwg.zeta.server.controller

import java.util.UUID

import scala.concurrent.Future

import akka.actor.ActorSystem
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGdslProjectRepository
import de.htwg.zeta.persistence.general.GraphicalDslInstanceRepository
import de.htwg.zeta.server.silhouette.ZetaEnv
import javax.inject.Inject

import scala.concurrent.ExecutionContext

import play.api.mvc.AnyContent
import play.api.mvc.InjectedController
import play.api.mvc.Result

class ModelController @Inject()(
    system: ActorSystem,
    silhouette: Silhouette[ZetaEnv],
    modelEntityRepo: GraphicalDslInstanceRepository,
    metaModelEntityRepo: AccessRestrictedGdslProjectRepository,
    implicit val ec: ExecutionContext
) extends InjectedController {

  def modelEditor(modelId: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    for {
      model <- modelEntityRepo.read(modelId)
      metaModelEntity <- metaModelEntityRepo.restrictedTo(request.identity.id).read(model.graphicalDslId)
    } yield {
      Ok(views.html.model.ModelGraphicalEditor(model, metaModelEntity, request.identity.user))
    }
  }

}
