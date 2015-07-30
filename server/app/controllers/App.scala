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

  def metaModelEditor() = SecuredAction { implicit request =>
    Ok(views.html.metaModelEditor.render())
  }

  def modelValidator() = SecuredAction { implicit request =>
    Ok(views.html.modelValidator.render())
  }

  def saveMetaModel() = SecuredAction { implicit request =>
    println(request.body.toString)
    request.body.asJson match {
      case Some(json) =>
        MetaModelDatabase.saveModel(new MetaModel(
          model = (json \ "data").as[JsValue].toString(),
          name = (json \ "name").as[String],
          uuid = UUID.randomUUID().toString,
          userUuid = request.user.uuid.toString))
        Ok("Saved.")
      case _ =>
        BadRequest("No valid Json Supplied.")
    }
  }

  def codeEditor() = SecuredAction { implicit request =>
    Ok(views.html.codeEditor.render(Some(request.user)))
  }

  def newGraph(metaModelId:String) = SecuredAction{ implicit request =>
      log.debug("Calling newGraph(" + metaModelId + ")")
      if (Await.result(MetaModelDatabase.modelExists(metaModelId), 30 seconds)) {
        Redirect(routes.App.editor(metaModelId, ShortUUID.uuid))
      }
      else {
        log.error("attempting to create an editor with unknown ecore type")
        Redirect(routes.App.index())
      }
  }

  def diagrams = SecuredAction{ implicit request =>
    val metaModels = Await.result(MetaModelDatabase.modelsOfUser(request.user.uuid.toString), 30 seconds)
    Ok(views.html.diagramsOverview.render(metaModels, Some(request.user)))
  }

  def editor(metaModelId:String, uuid:String) = SecuredAction{implicit request =>
    Ok(views.html.editor.render(metaModelId, uuid, request.user.uuid.toString, request.user.profile.fullName.getOrElse("")))
  }

  def codeSocket = WebSocket.acceptWithActor[String, String] { request => out =>
    CodeWSActor.props(out)
  }

  def diagramSocket(instanceId:String) = WebSocket.acceptWithActor[String, String]{ request => out =>
    DiagramWSActor.props(out, instanceId)
  }
}
