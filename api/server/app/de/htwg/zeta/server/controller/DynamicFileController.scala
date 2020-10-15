package de.htwg.zeta.server.controller

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedFilePersistence
import de.htwg.zeta.server.silhouette.ZetaEnv
import play.api.mvc.AnyContent
import play.api.mvc.InjectedController
import play.api.mvc.Result

@Singleton
class DynamicFileController @Inject()(
    fileRepo: AccessRestrictedFilePersistence,
    implicit val ec: ExecutionContext
) extends InjectedController {

  def serveFile(id: UUID, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    fileRepo.restrictedTo(request.identity.id).read(id, name).map(file =>
      Ok(file.content) as JAVASCRIPT
    ).recover { case _ =>
      NotFound(s"File not found ${id.toString}/$name")
    }
  }

}
