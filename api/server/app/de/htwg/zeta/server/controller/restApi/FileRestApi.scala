package de.htwg.zeta.server.controller.restApi

import java.util.UUID

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.persistence.Persistence
import de.htwg.zeta.server.controller.restApi.format.FileFormat
import de.htwg.zeta.server.util.auth.ZetaEnv
import grizzled.slf4j.Logging
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result
import play.api.mvc.Results
import scalaoauth2.provider.OAuth2ProviderActionBuilders.executionContext

/**
 * RESTful API for File definitions
 */
class FileRestApi() extends Controller with Logging {
  private val repo = Persistence.fullAccessRepository.file

  /**
   * Get a single File instance
   * @param id Identifier of File
   * @param request The request
   * @return The result
   */
  def get(id: UUID, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    repo.read(id, name).flatMap(entity => {
      Future(Ok(FileFormat.writes(entity)))
    }).recover {
      case e: Exception =>
        error("Exception while trying to read a single `File` from DB", e)
        Results.BadRequest(e.getMessage)
    }
  }
}
