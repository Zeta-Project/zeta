package controllers.webpage

import models._
import play.api.Logger
import securesocial.core.RuntimeEnvironment

import scala.concurrent.Await
import scala.concurrent.duration._

class Webpage(override implicit val env: RuntimeEnvironment[SecureSocialUser])
  extends securesocial.core.SecureSocial[SecureSocialUser] {

  val log = Logger(this getClass() getName())

  def index() = SecuredAction { implicit request =>
    Ok(views.html.webpage.WebpageIndex.render(Some(request.user)))
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

    Ok(views.html.webpage.WebpageDiagramsOverview.render(Some(request.user), Some(metaModels), metaModel))
  }
}
