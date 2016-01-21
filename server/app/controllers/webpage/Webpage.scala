package controllers.webpage

import javax.inject.Inject

import models._
import models.metaModel.{MetaModelDatabase, MetaModel}
import play.api.Logger
import securesocial.core.RuntimeEnvironment
import util.definitions.UserEnvironment

import scala.concurrent.Await
import scala.concurrent.duration._

class Webpage @Inject() (override implicit val env: UserEnvironment) extends securesocial.core.SecureSocial {

  val log = Logger(this getClass() getName())

  def index() = SecuredAction { implicit request =>
    Ok(views.html.webpage.WebpageIndex(Some(request.user)))
  }

  def diagramsOverview(uuid: String) = SecuredAction { implicit request =>
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

    Ok(views.html.webpage.WebpageDiagramsOverview(Some(request.user), Some(metaModels), metaModel))
  }
}
