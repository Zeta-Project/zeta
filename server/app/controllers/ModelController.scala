package controllers

import javax.inject.Inject

import dao.model.ZetaModelDao
import models.model.ModelWsActor
import models.modelDefinitions.model.ModelEntity
import play.api.Logger
import play.api.Play.current
import play.api.mvc.WebSocket
import util.definitions.UserEnvironment
import scala.concurrent.duration._
import scala.concurrent.Await

class ModelController @Inject()(override implicit val env: UserEnvironment, modelDao: ZetaModelDao) extends securesocial.core.SecureSocial {

  val log = Logger(this getClass() getName())

  def newModel(metaModelUuid: String) = SecuredAction { implicit request =>
    log.debug("Calling newModel(" + metaModelUuid + ")")
    /*if (Await.result(MetaModelDatabase_2.modelExists(metaModelUuid), 30 seconds)) {
      Redirect(routes.ModelController.modelEditor(metaModelUuid, ShortUuid.uuid))
    } else {
      log.error("attempting to create an editor with unknown uuid")
      Redirect(controllers.webpage.routes.WebpageController.index())
    }*/
    BadRequest("TODO: connect Model Editor to new Metamodel REST API")
  }

  def modelEditor(metaModelUuid: String, modelUuid: String) = SecuredAction { implicit request =>
    val model = Await.result(modelDao.findById(modelUuid), 30 seconds)
    if(!model.isDefined) BadRequest("no model available for this modelid")
    Ok(views.html.model.ModelGraphicalEditor(metaModelUuid, modelUuid, Some(request.user), model.get))
  }

  def modelValidator() = SecuredAction { implicit request =>
    Ok(views.html.model.ModelValidator(Some(request.user)))
  }

  def modelSocket(instanceId: String, graphType: String) = WebSocket.acceptWithActor[String, String] { request => out =>
    ModelWsActor.props(out, instanceId, graphType)
  }
}
