package controllers

import models._
import play.api.Logger
import play.api.Play.current
import play.api.mvc.WebSocket
import securesocial.core.RuntimeEnvironment

import scala.concurrent.Await
import scala.concurrent.duration._

class Webpage(override implicit val env: RuntimeEnvironment[SecureSocialUser])

  extends securesocial.core.SecureSocial[SecureSocialUser] {

  val log = Logger(this getClass() getName())

  def index() = SecuredAction { implicit request =>
    Ok(views.html.webpage.WebpageIndex.render(Some(request.user)))
  }







  def newGraph(metaModelId:String) = SecuredAction{ implicit request =>
      log.debug("Calling newGraph(" + metaModelId + ")")
      if (Await.result(MetaModelDatabase.modelExists(metaModelId), 30 seconds)) {
        Redirect(routes.Webpage.editor(metaModelId, ShortUUID.uuid))
      }
      else {
        log.error("attempting to create an editor with unknown ecore type")
        Redirect(routes.Webpage.index())
      }
  }

  def diagrams = SecuredAction{ implicit request =>
    val metaModels = Await.result(MetaModelDatabase.modelsOfUser(request.user.uuid.toString), 30 seconds)
    Ok(views.html.diagramsOverview.render(metaModels, Some(request.user)))
  }

  def editor(metaModelId:String, uuid:String) = SecuredAction{implicit request =>
    Ok(views.html.model.ModelGraphicalEditor.render(metaModelId, uuid, request.user.uuid.toString, request.user.profile.fullName.getOrElse("")))
  }

  def codeSocket = WebSocket.acceptWithActor[String, String] { request => out =>
    CodeDocWSActor.props(out, CodeDocManagingActor.getCodeDocManager, "fakeDiagramId")
  }

  def diagramSocket(instanceId:String, graphType:String) = WebSocket.acceptWithActor[String, String]{ request => out =>
    DiagramWSActor.props(out, instanceId, graphType)
  }
}
