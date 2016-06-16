package controllers.webpage

import javax.inject.Inject

import dao.metaModel.MetaModelDaoImpl
import models.metaModel.MetaModelEntity
import play.api.Logger
import util.definitions.UserEnvironment

import scala.concurrent.Await
import scala.concurrent.duration._

class WebpageController @Inject()(override implicit val env: UserEnvironment) extends securesocial.core.SecureSocial {

  val log = Logger(this getClass() getName())

  def index() = SecuredAction { implicit request =>
    Redirect("/overview")
  }

  def diagramsOverview(uuid: String) = SecuredAction { implicit request =>

    val metaModels = Await.result(MetaModelDaoImpl.findIdsByUser(request.user.uuid.toString), 30 seconds)

    var metaModel: Option[MetaModelEntity] = None

    if (uuid != null) {
      val hasAccess = Await.result(MetaModelDaoImpl.hasAccess(uuid, request.user.uuid.toString), 30 seconds)
      if (hasAccess.isDefined && hasAccess.get) {
        metaModel = Await.result(MetaModelDaoImpl.findById(uuid), 30 seconds)
      }
    }

    Ok(views.html.webpage.WebpageDiagramsOverview(Some(request.user), metaModels, metaModel))
  }
}
