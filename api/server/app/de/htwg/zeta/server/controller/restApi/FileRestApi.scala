package de.htwg.zeta.server.controller.restApi

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.format.entity.FileFormat
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.persistence.general.FileRepository
import de.htwg.zeta.server.silhouette.ZetaEnv
import grizzled.slf4j.Logging
import play.api.libs.json.JsError
import play.api.libs.json.JsPath
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.JsonValidationError
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result


/**
 * REST-ful API for File definitions
 */
class FileRestApi @Inject()(
    fileRepo: FileRepository,
    fileFormat: FileFormat
) extends Controller with Logging {

  /**
   * Get a single File instance
   * @param id Identifier of File
   * @param request The request
   * @return The result
   */
  def get(id: UUID, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    fileRepo.read(id, name).flatMap(entity => {
      Future(Ok(fileFormat.writes(entity)))
    }).recover {
      case e: Exception =>
        error("Exception while trying to read a single `File` from DB", e)
        BadRequest(e.getMessage)
    }
  }

  /**
   * Update existing File instance
   * @param id Identifier of File
   * @param name Name of File
   * @param request The request
   * @return The result
   */
  def update(id: UUID, name: String)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    parseJson(request.body).flatMap(result => {
      result.fold(
        errors => jsErrorToResult(errors),
        file => fileRepo.update(file).map(_ => Ok("")).recover {
          case e: Exception =>
            error("Exception while trying to update a `File` at DB", e)
            BadRequest(e.getMessage)
        }
      )
    })
  }

  private def parseJson(json: JsValue) = {
    json.validate(fileFormat) match {
      case s: JsSuccess[File] => Future.successful(s)
      case e: JsError => Future.successful(e)
    }
  }

  private def jsErrorToResult(errors: Seq[(JsPath, Seq[JsonValidationError])]): Future[Result] = {
    val json = JsError.toJson(errors)
    val result = BadRequest(json)
    Future.successful(result)
  }
}
