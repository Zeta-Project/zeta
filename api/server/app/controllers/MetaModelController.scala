package controllers


import java.time.Instant
import javax.inject.Inject

import dao.metaModel.{ZetaMetaModelDao}
import models.metaModel._
import models.modelDefinitions.metaModel.{Dsl, MetaModel, MetaModelEntity}
import models.modelDefinitions.metaModel.elements.MObject
import play.api.Play.current
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc.WebSocket
import util.definitions.UserEnvironment
import util.graph.MetamodelGraphDiff

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by mgt on 17.10.15.
  */


class MetaModelController @Inject()(metaModelDao: ZetaMetaModelDao, override implicit val env: UserEnvironment) extends securesocial.core.SecureSocial {

  def metaModelEditor(metaModelUuid: String) = SecuredAction { implicit request =>
    val hasAccess = Await.result(metaModelDao.hasAccess(metaModelUuid, request.user.uuid.toString), 30 seconds)
    if (hasAccess.isDefined && hasAccess.get) {
      val metaModelEntity = Await.result(metaModelDao.findById(metaModelUuid), 30 seconds)
      if (metaModelEntity.isDefined) {

        // Fix Graph with MetaModelGraphDiff
        //val oldMetaModelEntity = metaModelEntity.get
        //val fixedConcept = MetamodelGraphDiff.fixGraph(oldMetaModelEntity.metaModel)
        //val fixedDefinition = oldMetaModelEntity.metaModel.copy(concept = fixedConcept)
        //val fixedMetaModelEntity = oldMetaModelEntity.copy(metaModel = fixedConcept)

        Ok(views.html.metamodel.MetaModelGraphicalEditor(Some(request.user), metaModelUuid, metaModelEntity.get))
      } else {
        BadRequest("Could not find meta model")
      }
    } else {
      Unauthorized("Unauthorized")
    }
  }

  def metaModelSocket(metaModelUuid: String) = WebSocket.acceptWithActor[JsValue, JsValue] { request => out =>
    MetaModelWsActor.props(out, metaModelUuid)
  }





}
