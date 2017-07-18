package de.htwg.zeta.server.controller.restApi

import java.util.UUID

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.models.entity.BondedTask
import de.htwg.zeta.persistence.Persistence
import de.htwg.zeta.server.controller.restApi.format.BondedTaskFormat
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
class BondedTaskRestApi() extends Controller with Logging {

  private val repo = Persistence.fullAccessRepository.bondedTask

  /** Lists all filter.
   *
   * @param request The request
   * @return The result
   */
  def showForUser()(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    getEntities.map(getResultJsonArray).recover {
      case e: Exception =>
        error("Exception while trying to read all `BondedTask` from DB", e)
        BadRequest(e.getMessage)
    }
  }

  private def getEntities: Future[List[BondedTask]] = {
    repo.readAllIds().flatMap(ids => {
      val entities = ids.toList.map(repo.read)
      Future.sequence(entities)
    })
  }

  private def getResultJsonArray(list: List[BondedTask]) = {
    val entities = list.filter(e => !e.deleted.getOrElse(false))
    val entries = entities.map(BondedTaskFormat.writes)
    val json = JsArray(entries)
    Ok(json)
  }

  /**
   * Flag BondedTask as deleted
   * @param id Identifier of BondedTask
   * @param request The request
   * @return The result
   */
  def delete(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    flagAsDeleted(id).map(_ => Ok("")).recover {
      case e: Exception =>
        error("Exception while trying to flag `BondedTask` as deleted at DB", e)
        BadRequest(e.getMessage)
    }
  }

  private def flagAsDeleted(id: UUID): Future[BondedTask] = {
    val deleted = Some(true)
    repo.update(id, e => e.copy(deleted = deleted))
  }
}
