package controllers


import java.util.UUID
import javax.inject.Inject


import dao.metaModel.MetaModelDao
import models.metaModel._
import models.modelDefinitions.metaModel.MetaModelEntity
import play.api.Play.current
import play.api.libs.json.{JsError, Json, JsValue}
import play.api.mvc.{Action, WebSocket}
import util.definitions.UserEnvironment
import util.graph.MetamodelGraphDiff

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.util.Try

/**
  * Created by mgt on 17.10.15.
  */


class MetaModelController @Inject()(override implicit val env: UserEnvironment) extends securesocial.core.SecureSocial {

  def newMetaModel() = SecuredAction { implicit request =>
    Redirect(routes.MetaModelController.metaModelEditor(UUID.randomUUID.toString))
  }

  def metaModelEditor(metaModelUuid: String) = SecuredAction { implicit request =>
    var metaModel: Option[MetaModel_2] = None
    if (Await.result(MetaModelDatabase_2.modelExists(metaModelUuid), 30 seconds)) {
      val tmpMetaModel = Await.result(MetaModelDatabase_2.loadModel(metaModelUuid), 30 seconds)
      if (tmpMetaModel.get.userUuid == request.user.uuid.toString) {
        metaModel = Some(tmpMetaModel.get.copy(metaModel = MetamodelGraphDiff.fixMetaModel(tmpMetaModel.get.metaModel)))
      }
    }
    Ok(views.html.metamodel.MetaModelGraphicalEditor(Some(request.user), metaModelUuid, metaModel))
  }

  def saveMetaModel() = SecuredAction { implicit request =>
    println(request.body.asJson)
    request.body.asJson match {
      case Some(json) =>
        Try(UUID.fromString((json \ "uuid").as[String])).toOption match {
          case Some(uuid) =>
            val uuidStr = uuid.toString

            val metaModelData = new MetaModelData_2(
              name = (json \ "name").as[String],
              data = (json \ "data").as[JsValue].toString(),
              graph = (json \ "graph").as[JsValue].toString()
            )

            if (Await.result(MetaModelDatabase_2.modelExists(uuidStr), 30 seconds)) {
              // Change the existing Meta Model
              MetaModelDatabase_2.updateMetaModelData(uuidStr, metaModelData)
            } else {
              // Create a new Meta Model
              MetaModelDatabase_2.saveModel(new MetaModel_2(
                uuid = uuidStr,
                userUuid = request.user.uuid.toString,
                metaModel = metaModelData,
                style = new MetaModelStyle_2,
                shape = new MetaModelShape_2,
                diagram = new MetaModelDiagram_2
              ))

            }

            Ok("Saved")
          case None => BadRequest("Invalid UUID")
        }
      case None => BadRequest("Invalid JSON")
    }
  }

  def deleteMetaModel(metaModelUuid: String) = SecuredAction { implicit request =>
    val userUuid = request.user.uuid.toString
    if (Await.result(MetaModelDatabase_2.modelExists(metaModelUuid), 30 seconds)) {
      val metaModel = Await.result(MetaModelDatabase_2.loadModel(metaModelUuid), 30 seconds)
      if (metaModel.isDefined && metaModel.get.userUuid == userUuid) {
        MetaModelDatabase_2.deleteModel(metaModelUuid)

        Redirect(controllers.webpage.routes.WebpageController.diagramsOverview(null))
      } else {
        Redirect(controllers.webpage.routes.WebpageController.index())
      }
    } else {
      Redirect(controllers.webpage.routes.WebpageController.index())
    }
  }

  def metaModelSocket(metaModelUuid: String) = WebSocket.acceptWithActor[JsValue, JsValue] { request => out =>
    MetaModelWsActor.props(out, metaModelUuid)
  }






}
