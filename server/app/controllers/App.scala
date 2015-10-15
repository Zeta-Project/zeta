package controllers

import java.util.UUID

import models._
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
    Ok(views.html.metaModelEditor.render(uuid))
  }

  def newMetaModel() = SecuredAction { implicit request =>
    Redirect(routes.App.metaModelEditor(UUID.randomUUID().toString))
  }

  def modelValidator() = SecuredAction { implicit request =>
    Ok(views.html.modelValidator.render())
  }

  def saveMetaModel() = SecuredAction { implicit request =>
    println(request.body.toString)
    request.body.asJson match {
      case Some(json) => {
        val data = (json \ "data").as[JsValue].toString()
        val graph = (json \ "graph").as[JsValue].toString()
        val name = (json \ "name").as[String]
        val uuid = (json \ "uuid").as[String]
        val userUuid = request.user.uuid.toString

        MetaModelDatabase.saveModel(new MetaModel(
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
        ))
        Ok("Saved.")
      }

      case _ =>
        BadRequest("No valid Json Supplied.")
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
    if (uuid != null) {
      val metaModelExists = Await.result(MetaModelDatabase.modelExists(uuid), 30 seconds)
      if (metaModelExists) {
        val metaModel = Await.result(MetaModelDatabase.loadModel(uuid), 30 seconds).get
        Ok(views.html.diagramsOverview.render(Some(request.user), metaModels, metaModel))
      } else {
        Ok(views.html.diagramsOverview.render(Some(request.user), metaModels, null))
      }
    } else {
      Ok(views.html.diagramsOverview.render(Some(request.user), metaModels, null))
    }

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
