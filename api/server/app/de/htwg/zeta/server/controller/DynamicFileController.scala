package de.htwg.zeta.server.controller

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.persistence.Persistence.restrictedAccessRepository
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result

class DynamicFileController extends Controller {

  def serveFile(id: UUID, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    restrictedAccessRepository(request.identity.id).file.read(id, name).map(file =>
      Ok(file.content) as JAVASCRIPT
    ).recover { case _ =>
      NotFound(s"File not found ${id.toString}/$name")
    }
  }

}
