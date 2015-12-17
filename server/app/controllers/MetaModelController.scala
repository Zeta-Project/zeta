package controllers


import java.util.UUID

import argonaut.Argonaut._
import argonaut.DecodeJson
import models._
import models.metaModel._
import modigen.util.graph.MetamodelGraphDiff
import play.api.Play.current
import play.api.libs.json.JsValue
import play.api.mvc.WebSocket
import securesocial.core.RuntimeEnvironment

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try

/**
  * Created by mgt on 17.10.15.
  */


class MetaModelController(override implicit val env: RuntimeEnvironment[SecureSocialUser])
  extends securesocial.core.SecureSocial[SecureSocialUser] {

  def newMetaModel() = SecuredAction { implicit request =>
    Redirect(routes.MetaModelController.metaModelEditor(UUID.randomUUID.toString))
  }

  def metaModelEditor(metaModelUuid: String) = SecuredAction { implicit request =>
    var metaModel: Option[MetaModel] = None
    if (Await.result(MetaModelDatabase.modelExists(metaModelUuid), 30 seconds)) {
      val tmpMetaModel = Await.result(MetaModelDatabase.loadModel(metaModelUuid), 30 seconds)
      if (tmpMetaModel.get.userUuid == request.user.uuid.toString) {
        metaModel = Some(tmpMetaModel.get.copy(metaModel = MetamodelGraphDiff.fixMetaModel(tmpMetaModel.get.metaModel)))
      }
    }
    Ok(views.html.metamodel.MetaModelGraphicalEditor.render(Some(request.user), metaModelUuid, metaModel))
  }

  def saveMetaModel() = SecuredAction { implicit request =>
    println(request.body.asJson)
    request.body.asJson match {
      case Some(json) =>
        Try(UUID.fromString((json \ "uuid").as[String])).toOption match {
          case Some(uuid) =>
            val uuidStr = uuid.toString

            val metaModelData = new MetaModelData(
              name = (json \ "name").as[String],
              data = (json \ "data").as[JsValue].toString(),
              graph = (json \ "graph").as[JsValue].toString()
            )

            if (Await.result(MetaModelDatabase.modelExists(uuidStr), 30 seconds)) {
              // Change the existing Meta Model
              MetaModelDatabase.updateMetaModelData(uuidStr, metaModelData)
            } else {
              // Create a new Meta Model
              MetaModelDatabase.saveModel(new MetaModel(
                uuid = uuidStr,
                userUuid = request.user.uuid.toString,
                metaModel = metaModelData,
                style = new MetaModelStyle,
                shape = new MetaModelShape,
                diagram = new MetaModelDiagram
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
    if (Await.result(MetaModelDatabase.modelExists(metaModelUuid), 30 seconds)) {
      val metaModel = Await.result(MetaModelDatabase.loadModel(metaModelUuid), 30 seconds)
      if (metaModel.isDefined && metaModel.get.userUuid == userUuid) {
        MetaModelDatabase.deleteModel(metaModelUuid)

        Redirect(controllers.webpage.routes.Webpage.diagramsOverview(null))
      } else {
        Redirect(controllers.webpage.routes.Webpage.index())
      }
    } else {
      Redirect(controllers.webpage.routes.Webpage.index())
    }
  }

  def metaModelSocket(metaModelUuid: String) = WebSocket.acceptWithActor[JsValue, JsValue] { request => out =>
    MetaModelWsActor.props(out, metaModelUuid)
  }

  /** Argonaut Conversions */
  implicit def MetaModelDefinitionJson: DecodeJson[MetaModelDefinition] =
    DecodeJson(c => for {
      name <- (c --\ "Class").as[String]

    } yield MetaModelDefinition(mClasses = null, mReferences = null, mEnums = null))

}
