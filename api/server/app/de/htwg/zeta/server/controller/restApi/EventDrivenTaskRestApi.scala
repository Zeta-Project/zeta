package de.htwg.zeta.server.controller.restApi

import java.util.UUID

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.models.entity.EventDrivenTask
import de.htwg.zeta.persistence.Persistence
import de.htwg.zeta.server.controller.restApi.format.EventDrivenTaskFormat
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.libs.json.JsArray
import play.api.libs.json.JsValue
import play.api.mvc.AnyContent
import play.api.mvc.Result
import scalaoauth2.provider.OAuth2ProviderActionBuilders.executionContext

/**
 * RESTful API for filter definitions
 */
class EventDrivenTaskRestApi() extends RestApiController[EventDrivenTask] {

  private val repo = Persistence.fullAccessRepository.eventDrivenTask

  /** Lists all filter.
   *
   * @param request The request
   * @return The result
   */
  def showForUser()(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    getEntities.map(getResultJsonArray).recover {
      case e: Exception =>
        error("Exception while trying to read all `EventDrivenTask` from DB", e)
        BadRequest(e.getMessage)
    }
  }

  private def getEntities: Future[List[EventDrivenTask]] = {
    repo.readAllIds().flatMap(ids => {
      val list = ids.toList.map(repo.read)
      Future.sequence(list)
    })
  }

  private def getResultJsonArray(list: List[EventDrivenTask]) = {
    val entities = list.filter(e => !e.deleted.getOrElse(false))
    val entries = entities.map(EventDrivenTaskFormat.writes)
    val json = JsArray(entries)
    Ok(json)
  }

  /**
   * Flag EventDrivenTask as deleted
   * @param id Identifier of EventDrivenTask
   * @param request The request
   * @return The result
   */
  def delete(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    flagAsDeleted(id).map(_ => Ok("")).recover {
      case e: Exception =>
        error("Exception while trying to flag `EventDrivenTask` as deleted at DB", e)
        BadRequest(e.getMessage)
    }
  }

  private def flagAsDeleted(id: UUID): Future[EventDrivenTask] = {
    val deleted = Some(true)
    repo.update(id, e => e.copy(deleted = deleted))
  }

  /**
   * Add new BondedTask into DB
   * @param request The request
   * @return The result
   */
  def insert(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    parseJson(request.body, EventDrivenTaskFormat, (entity) => repo.create(entity).map(_ => Ok("")))
  }
}
