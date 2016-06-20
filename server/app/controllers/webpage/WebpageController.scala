package controllers.webpage

import javax.inject.Inject

import dao.metaModel.ZetaMetaModelDao
import dao.model.ZetaModelDao
import models.modelDefinitions.metaModel.MetaModelEntity
import models.modelDefinitions.model.ModelShortInfo
import play.api.Logger
import util.definitions.UserEnvironment

import scala.concurrent.Await
import scala.concurrent.duration._

class WebpageController @Inject()(metaModelDao: ZetaMetaModelDao, modelDao: ZetaModelDao, override implicit val env: UserEnvironment) extends securesocial.core.SecureSocial {

  val log = Logger(this getClass() getName())

  def index() = SecuredAction { implicit request =>
    Redirect("/overview")
  }

  def diagramsOverview(uuid: String) = SecuredAction { implicit request =>

    val metaModels = Await.result(metaModelDao.findMetaModelsByUser(request.user.uuid.toString), 30 seconds)

    var metaModel: Option[MetaModelEntity] = None
    var models: Seq[ModelShortInfo] = Seq.empty[ModelShortInfo]
    if (uuid != null) {
      val hasAccess = Await.result(metaModelDao.hasAccess(uuid, request.user.uuid.toString), 30 seconds)
      if (hasAccess.isDefined && hasAccess.get) {
        metaModel = Await.result(metaModelDao.findById(uuid), 30 seconds)
        models = Await.result(modelDao.findModelsByUser(request.user.uuid.toString), 30 seconds).filter(m => m.metaModelId == uuid)
      }
    }

    Ok(views.html.webpage.WebpageDiagramsOverview(Some(request.user), metaModels, metaModel, models))
  }
}
