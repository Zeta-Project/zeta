package de.htwg.zeta.server.controller.restApi

import java.util.UUID

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.persistence.Persistence
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

  private val repo = Persistence.fullAccessRepository.generator

  /**
   * Lists all generator.
   * @param request The request
   * @return The result
   */
  def showForUser()(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    getEntities.map(getResultJsonArray).recover {
      case e: Exception =>
        error("Exception while trying to read all `Generator` from DB", e)
        BadRequest(e.getMessage)
    }
  }

  private def getEntities: Future[List[Generator]] = {
    repo.readAllIds().flatMap(ids => {
      val entities = ids.toList.map(repo.read)
      Future.sequence(entities)
    })
  }

  private def getResultJsonArray(list: List[Generator]): Result = {
    val entities = list.filter(e => !e.deleted)
    val entries = entities.map(GeneratorFormat.writes)
    val json = JsArray(entries)
    Ok(json)
  }

  /**
   * Get a single Generator instance
   * @param id Identifier of Generator
   * @param request The request
   * @return The result
   */
  def get(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    repo.read(id).map(getResultJsonValue).recover {
      case e: Exception =>
        error("Exception while trying to read a single `Generator` from DB", e)
        BadRequest(e.getMessage)
    }
  }

  private def getResultJsonValue(entity: Generator) = {
    val json = GeneratorFormat.writes(entity)
    Ok(json)
  }

  /**
   * Flag Generator instance as deleted
   * @param id Identifier of Generator
   * @param request The request
   * @return The result
   */
  def delete(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    flagAsDeleted(id).map(_ => Ok("")).recover {
      case e: Exception =>
        error("Exception while trying to flag `Generator` as deleted at DB", e)
        BadRequest(e.getMessage)
    }
  }

  private def flagAsDeleted(id: UUID): Future[Generator] = {
    repo.update(id, entity => entity.copy(deleted = true))
  }
}
