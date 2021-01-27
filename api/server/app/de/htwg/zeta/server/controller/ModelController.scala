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

import de.htwg.zeta.common.format.entity.UserFormat
import de.htwg.zeta.common.format.model.GraphicalDslInstanceFormat
import de.htwg.zeta.common.format.project.GdslProjectFormat
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.mvc.AnyContent
import play.api.mvc.InjectedController
import play.api.mvc.Result

class ModelController @Inject()(
    system: ActorSystem,
    silhouette: Silhouette[ZetaEnv],
    graphicalDslInstanceFormat: GraphicalDslInstanceFormat,
    gdslProjectFormat: GdslProjectFormat,
    userFormat: UserFormat,
    modelEntityRepo: GraphicalDslInstanceRepository,
    metaModelEntityRepo: AccessRestrictedGdslProjectRepository,
    implicit val ec: ExecutionContext
) extends InjectedController {

  def modelEditor(modelId: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    for {
      model <-  modelEntityRepo.read(modelId)
      metaModelEntity <- metaModelEntityRepo.restrictedTo(request.identity.id).read(model.graphicalDslId)
    } yield {
      val gDsLInstance = graphicalDslInstanceFormat.writes(model)
      val gDslProject = gdslProjectFormat.writes(metaModelEntity)
      val user = userFormat.writes(request.identity.user)
      Ok{
        Json.obj(
          "gDsLInstance" -> gDsLInstance,
          "gDslProject" -> gDslProject,
          "user" -> user
        )
      }
    }
  }

}
