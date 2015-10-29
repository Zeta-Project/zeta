package controllers

import java.util.UUID

import models._
import modigen.util.graph.MetamodelGraphDiff
import play.api.Logger
import play.api.libs.json.JsValue
import play.api.mvc.WebSocket
import securesocial.core.RuntimeEnvironment
import play.api.Play.current

import scala.concurrent.Await
import scala.concurrent.duration._


class App(override implicit val env: RuntimeEnvironment[SecureSocialUser])

  extends securesocial.core.SecureSocial[SecureSocialUser] {

  val log = Logger(this getClass() getName())

  def index() = SecuredAction { implicit request =>
    Ok(views.html.index.render(Some(request.user)))
  }

  def metaModelEditor(uuid: String) = SecuredAction { implicit request =>
    if (Await.result(MetaModelDatabase.modelExists(uuid), 30 seconds)) {
      val metaModel = Await.result(MetaModelDatabase.loadModel(uuid), 30 seconds).get
      if (metaModel.userUuid == request.user.uuid.toString) {

        // Fix Graph if something changed in the Meta Model
        val metaModelData = MetamodelGraphDiff.fixMetaModel(metaModel.metaModel)
        val newModel = metaModel.copy(metaModel = metaModelData)
        // MetaModelDatabase.saveModel(newModel) <-- Direkt abspeichern?

        Ok(views.html.metaModelEditor.render(uuid, Some(newModel)))
      } else {
        Redirect(routes.App.index())
      }
    } else {
      Ok(views.html.metaModelEditor.render(uuid, None))
    }
  }

  def newMetaModel() = SecuredAction { implicit request =>
    Ok(views.html.metaModelEditor.render(UUID.randomUUID.toString, None))
  }

  def modelValidator() = SecuredAction { implicit request =>
    Ok(views.html.modelValidator.render())
  }

  def saveMetaModel() = SecuredAction { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        val uuid = (json \ "uuid").as[String]

        var validUuid = true
        try {
          UUID.fromString(uuid)
        } catch {
          case _: IllegalArgumentException => validUuid = false
        }

        if (validUuid) {
          val data = (json \ "data").as[JsValue].toString()
          val graph = (json \ "graph").as[JsValue].toString()
          val name = (json \ "name").as[String]
          val userUuid = request.user.uuid.toString

          val metaModel = new MetaModel(
            uuid = uuid,
            userUuid = userUuid,
            metaModel = new MetaModelData(
              name = name,
              data = data,
              graph = graph
            ),
            style = new MetaModelStyle,
            shape = new MetaModelShape,
            diagram = new MetaModelDiagram
          )

          MetaModelDatabase.saveModel(metaModel)
          Ok("Saved.")
        } else {
          BadRequest("Invalid UUID")
        }

      case _ =>
        BadRequest("No valid Json Supplied.")
    }
  }

  def deleteMetaModel(uuid: String) = SecuredAction { implicit request =>
    val userUuid = request.user.uuid.toString
    if (Await.result(MetaModelDatabase.modelExists(uuid), 30 seconds)) {
      val metaModel = Await.result(MetaModelDatabase.loadModel(uuid), 30 seconds)
      if (metaModel.isDefined && metaModel.get.userUuid == userUuid) {
        MetaModelDatabase.deleteModel(uuid)
        Redirect(routes.App.diagrams(null))
      } else {
        Redirect(routes.App.index())
      }
    } else {
      Redirect(routes.App.index())
    }
  }

  def codeEditor() = SecuredAction { implicit request =>
    Ok(views.html.codeEditor.render(Some(request.user)))
  }

  def newGraph(metaModelId: String) = SecuredAction { implicit request =>
    log.debug("Calling newGraph(" + metaModelId + ")")
    if (Await.result(MetaModelDatabase.modelExists(metaModelId), 30 seconds)) {
      Redirect(routes.App.editor(metaModelId, ShortUUID.uuid))
    }
    else {
      log.error("attempting to create an editor with unknown ecore type")
      Redirect(routes.App.index())
    }
  }

  def diagrams(uuid: String) = SecuredAction { implicit request =>
    val metaModels = Await.result(MetaModelDatabase.modelsOfUser(request.user.uuid.toString), 30 seconds)
    var metaModel: Option[MetaModel] = None

    if (uuid != null) {
      if (Await.result(MetaModelDatabase.modelExists(uuid), 30 seconds)) {
        val dbMetaModel = Await.result(MetaModelDatabase.loadModel(uuid), 30 seconds).get
        if (dbMetaModel.userUuid == request.user.uuid.toString) {
          metaModel = Some(dbMetaModel)
        }
      }
    }

    Ok(views.html.diagramsOverview.render(Some(request.user), Some(metaModels), metaModel))

  }

  def editor(metaModelId: String, uuid: String) = SecuredAction { implicit request =>
    Ok(views.html.editor.render(metaModelId, uuid, request.user.uuid.toString, request.user.profile.fullName.getOrElse("")))
  }

  def codeSocket = WebSocket.acceptWithActor[String, String] { request => out =>
    CodeDocWSActor.props(out, CodeDocManagingActor.getCodeDocManager, "fakeDiagramId")
  }

  def diagramSocket(instanceId: String, graphType: String) = WebSocket.acceptWithActor[String, String]{ request => out =>
    DiagramWSActor.props(out, instanceId, graphType)
  }
}
