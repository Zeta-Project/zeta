package de.htwg.zeta.server.controller.restApi

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.format.entity.GeneratorImageFormat
import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.persistence.general.GeneratorImageRepository
import de.htwg.zeta.server.silhouette.ZetaEnv
import grizzled.slf4j.Logging
import play.api.libs.json.JsArray
import play.api.mvc.AnyContent
import play.api.mvc.InjectedController
import play.api.mvc.Result


/**
 * REST-ful API for generator image definitions
 */
class GeneratorImageRestApi @Inject()(
    generatorImageRepo: GeneratorImageRepository,
    generatorImageFormat: GeneratorImageFormat,
    implicit val ec: ExecutionContext
) extends InjectedController with Logging {

  /** Lists all generator images.
   *
   * @param request The request
   * @return The result
   */
  def showForUser()(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    generatorImageRepo.readAllIds().flatMap(getIds).map(getJsonArray).recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  private def getIds(ids: Set[UUID]) = {
    val list = ids.toList.map(generatorImageRepo.read)
    Future.sequence(list)
  }

  private def getJsonArray(list: List[GeneratorImage]) = {
    val entries = list.map(generatorImageFormat.writes)
    val json = JsArray(entries)
    Ok(json)
  }
}
