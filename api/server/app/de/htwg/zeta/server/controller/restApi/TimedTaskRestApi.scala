package de.htwg.zeta.server.controller.restApi

import java.util.UUID

import scala.concurrent.Future
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.models.entity.TimedTask
import de.htwg.zeta.persistence.Persistence
import de.htwg.zeta.persistence.general.EntityPersistence
import de.htwg.zeta.server.controller.restApi.format.TimedTaskFormat
import de.htwg.zeta.server.util.auth.ZetaEnv
import grizzled.slf4j.Logging
import play.api.libs.json.JsArray
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result

import scalaoauth2.provider.OAuth2ProviderActionBuilders.executionContext

/**
 * RESTful API for filter definitions
 */
class TimedTaskRestApi() extends Controller with Logging {

  /** Lists all filter.
   *
   * @param request The request
   * @return The result
   */
  def showForUser()(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    val repo = Persistence.fullAccessRepository.timedTask
    repo.readAllIds().flatMap(getIds(repo)).map(getJsonArray).recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  private def getIds(repo: EntityPersistence[TimedTask])(ids: Set[UUID]) = {
    val list = ids.toList.map(repo.read)
    Future.sequence(list)
  }

  private def getJsonArray(list: List[TimedTask]) = {
    val entries = list.map(TimedTaskFormat.writes)
    val json = JsArray(entries)
    Ok(json)
  }
}
