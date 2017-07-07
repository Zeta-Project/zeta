package de.htwg.zeta.server.controller.restApi

import java.util.UUID

import scala.concurrent.Future
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.persistence.Persistence
import de.htwg.zeta.persistence.general.EntityPersistence
import de.htwg.zeta.server.controller.restApi.format.GeneratorFormat
import de.htwg.zeta.server.util.auth.ZetaEnv
import grizzled.slf4j.Logging
import play.api.libs.json.JsArray
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result

import scalaoauth2.provider.OAuth2ProviderActionBuilders.executionContext

/**
 * RESTful API for generator definitions
 */
class GeneratorRestApi() extends Controller with Logging {

  /** Lists all generator.
   *
   * @param request The request
   * @return The result
   */
  def showForUser()(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    val repo = Persistence.fullAccessRepository.generator
    repo.readAllIds().flatMap(getIds(repo)).map(getJsonArray).recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  private def getIds(repo: EntityPersistence[Generator])(ids: Set[UUID]) = {
    val list = ids.toList.map(repo.read)
    Future.sequence(list)
  }

  private def getJsonArray(list: List[Generator]) = {
    val entries = list.map(GeneratorFormat.writes)
    val json = JsArray(entries)
    Ok(json)
  }
}
