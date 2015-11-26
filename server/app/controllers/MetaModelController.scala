package controllers


import java.util.UUID

import argonaut.Argonaut._
import argonaut.DecodeJson
import models._
import modigen.util.graph.MetamodelGraphDiff
import play.api.libs.json.JsValue
import securesocial.core.RuntimeEnvironment

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try

/**
  * Created by mgt on 17.10.15.
  */


class MetaModelController(override implicit val env: RuntimeEnvironment[SecureSocialUser])
  extends securesocial.core.SecureSocial[SecureSocialUser] {

  def codeEditor(metaModelUuid: String, dslType: String) = SecuredAction { implicit request =>
    Ok(views.html.metamodel.MetaModelCodeEditor.render(Some(request.user), metaModelUuid, dslType))
  }

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

  def modelValidator() = SecuredAction { implicit request =>
    Ok(views.html.model.modelValidator.render())
  }

  def saveMetaModel() = SecuredAction { implicit request =>
    println(request.body.asJson)
    request.body.asJson match {
      case Some(json) =>
        Try(UUID.fromString((json \ "uuid").as[String])).toOption match {
          case Some(uuid) =>
            MetaModelDatabase.saveModel(new MetaModel(
              uuid = uuid.toString,
              userUuid = request.user.uuid.toString,
              metaModel = new MetaModelData(
                name = (json \ "name").as[String],
                data = (json \ "data").as[JsValue].toString(),
                graph = (json \ "graph").as[JsValue].toString()
              ),
              style = new MetaModelStyle,
              shape = new MetaModelShape,
              diagram = new MetaModelDiagram
            ))

            Ok("Saved")
          case None => BadRequest("Invalid UUID")
        }
    }
  }

  def deleteMetaModel(uuid: String) = SecuredAction { implicit request =>
    val userUuid = request.user.uuid.toString
    if (Await.result(MetaModelDatabase.modelExists(uuid), 30 seconds)) {
      val metaModel = Await.result(MetaModelDatabase.loadModel(uuid), 30 seconds)
      if (metaModel.isDefined && metaModel.get.userUuid == userUuid) {
        MetaModelDatabase.deleteModel(uuid)
        Redirect(routes.Webpage.diagrams(null))
      } else {
        Redirect(routes.Webpage.index())
      }
    } else {
      Redirect(routes.Webpage.index())
    }
  }

  /** Argonaut Conversions */
  implicit def MetaModelDefinitionJson: DecodeJson[MetaModelDefinition] =
    DecodeJson(c => for {
      name <- (c --\ "Class").as[String]

    } yield MetaModelDefinition(mClasses = null, mReferences = null, mEnums = null))

}
