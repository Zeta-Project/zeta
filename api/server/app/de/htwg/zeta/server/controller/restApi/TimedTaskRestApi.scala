package de.htwg.zeta.server.controller.restApi

import java.util.UUID

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.models.entity.TimedTask
import de.htwg.zeta.persistence.Persistence
import de.htwg.zeta.server.controller.restApi.format.TimedTaskFormat
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.libs.json.JsArray
import play.api.libs.json.JsValue
import play.api.mvc.AnyContent
import play.api.mvc.Result
import scalaoauth2.provider.OAuth2ProviderActionBuilders.executionContext

/**
 * RESTful API for filter definitions
 */
class TimedTaskRestApi() extends RestApiController[TimedTask] {

  private val repo = Persistence.fullAccessRepository.timedTask

  /** Lists all filter.
   *
   * @param request The request
   * @return The result
   */
  def showForUser()(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    getEntities.map(getJsonArray).recover {
      case e: Exception =>
        error("Exception while trying to read all `TimedTask` from DB", e)
        BadRequest(e.getMessage)
    }
  }

  private def getEntities: Future[List[TimedTask]] = {
    repo.readAllIds().flatMap(ids => {
      val list = ids.toList.map(repo.read)
      Future.sequence(list)
    })
  }

  private def getJsonArray(list: List[TimedTask]) = {
    val entities = list.filter(e => !e.deleted)
    val entries = entities.map(TimedTaskFormat.writes)
    val json = JsArray(entries)
    Ok(json)
  }

  /**
   * Flag TimedTask as deleted
   * @param id Identifier of TimedTask
   * @param request The request
   * @return The result
   */
  def delete(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    flagAsDeleted(id).map(_ => Ok("")).recover {
      case e: Exception =>
        error("Exception while trying to flag `TimedTask` as deleted at DB", e)
        BadRequest(e.getMessage)
    }
  }

  private def flagAsDeleted(id: UUID): Future[TimedTask] = {
    repo.update(id, e => e.copy(deleted = true))
  }

  /**
   * Add new BondedTask into DB
   * @param request The request
   * @return The result
   */
  def insert(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    parseJson(request.body, TimedTaskFormat, (entity) => repo.create(entity).map(_ => Ok("")))
  }
}