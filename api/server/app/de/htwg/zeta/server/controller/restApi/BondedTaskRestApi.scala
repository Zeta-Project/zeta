package de.htwg.zeta.server.controller.restApi

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.format.entity.BondedTaskFormat
import de.htwg.zeta.common.models.entity.BondedTask
import de.htwg.zeta.persistence.general.BondedTaskRepository
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.libs.json.JsValue
import play.api.libs.json.Writes
import play.api.mvc.AnyContent
import play.api.mvc.Result

/**
 * REST-ful API for bondedTask definitions
 */
class BondedTaskRestApi @Inject()(
    bondedTaskRepo: BondedTaskRepository,
    bondedTaskFormat: BondedTaskFormat
) extends RestApiController[BondedTask] {

  /** Lists all BondedTask.
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
    bondedTaskRepo.readAllIds().flatMap(ids => {
      val entities = ids.toList.map(bondedTaskRepo.read)
      Future.sequence(entities)
    })
  }

  private def getResultJsonArray(list: List[BondedTask]) = {
    val entities = list.filter(e => !e.deleted)
    Ok(Writes.list(bondedTaskFormat).writes(entities))
  }

  /**
   * Flag BondedTask as deleted
   *
   * @param id      Identifier of BondedTask
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
    bondedTaskRepo.update(id, e => e.copy(deleted = true))
  }

  /**
   * Add new BondedTask into DB
   *
   * @param request The request
   * @return The result
   */
  def insert(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    parseJson(request.body, bondedTaskFormat, (bondedTask) => bondedTaskRepo.create(bondedTask).map(_ => Ok("")))
  }
}
