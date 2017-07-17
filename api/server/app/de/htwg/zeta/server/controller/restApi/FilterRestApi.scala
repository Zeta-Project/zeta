package de.htwg.zeta.server.controller.restApi

import java.util.UUID

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.persistence.Persistence
import de.htwg.zeta.server.controller.restApi.format.FilterFormat
import de.htwg.zeta.server.util.auth.ZetaEnv
import grizzled.slf4j.Logging
import play.api.data.validation.ValidationError
import play.api.libs.json.JsArray
import play.api.libs.json.JsError
import play.api.libs.json.JsPath
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result
import play.api.mvc.Results
import scalaoauth2.provider.OAuth2ProviderActionBuilders.executionContext

/**
 * RESTful API for filter definitions
 */
class FilterRestApi() extends Controller with Logging {

  private val repo = Persistence.fullAccessRepository.filter

  /** Lists all filter.
   *
   * @param request The request
   * @return The result
   */
  def showForUser()(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    getEntities.map(getJsonArray).recover {
      case e: Exception =>
        error("Exception while trying to read all `Filter` from DB", e)
        BadRequest(e.getMessage)
    }
  }

  private def getEntities: Future[List[Filter]] = {
    repo.readAllIds().flatMap(ids => {
      val list = ids.toList.map(repo.read)
      Future.sequence(list)
    })
  }

  private def getJsonArray(list: List[Filter]) = {
    val entities = list.filter(e => !e.deleted)
    val entries = entities.map(FilterFormat.writes)
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
    val repo = Persistence.fullAccessRepository.filter
    repo.read(id).flatMap(entity => {
      Future(Ok(FilterFormat.writes(entity)))
    }).recover {
      case e: Exception =>
        error("Exception while trying to read a single `Filter` from DB", e)
        Results.BadRequest(e.getMessage)
    }
  }

  /**
   * Flag Filter as deleted
   * @param id Identifier of Filter
   * @param request The request
   * @return The result
   */
  def delete(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    flagAsDeleted(id).map(_ => Ok("")).recover {
      case e: Exception =>
        error("Exception while trying to flag `Filter` as deleted at DB", e)
        BadRequest(e.getMessage)
    }
  }

  private def flagAsDeleted(id: UUID): Future[Filter] = {
    repo.update(id, e => e.copy(deleted = true))
  }

  /**
   * Add new Filter into DB
   * @param request The request
   * @return The result
   */
  def insert(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    parseJson(request.body).flatMap(result => {
      result.fold(
        errors => jsErrorToResult(errors),
        filter => insertDb(filter).map(_ => Ok("")).recover {
          case e: Exception =>
            error("Exception while trying to insert a `Filter` into DB", e)
            BadRequest(e.getMessage)
        }
      )
    })
  }

  private def parseJson(json: JsValue): Future[JsResult[Filter]] = {
    json.validate(FilterFormat()) match {
      case s: JsSuccess[Filter] => Future.successful(s)
      case e: JsError => Future.successful(e)
    }
  }

  private def jsErrorToResult(errors: Seq[(JsPath, Seq[ValidationError])]): Future[Result] = {
    val json = JsError.toJson(errors)
    val result = BadRequest(json)
    Future.successful(result)
  }

  private def insertDb(entity: Filter): Future[Filter] = {
    for {
      file <- createFile("filter.scala")
      filter <- createFilter(entity, file)
    } yield {
      filter
    }
  }

  private def createFile(name: String): Future[File] = {
    val file = File(
      id = UUID.randomUUID(),
      name,
      content = fileTemplate()
    )
    Persistence.fullAccessRepository.file.create(file)
  }

  private def fileTemplate(): String = {
    s"""
      |class Filter() extends BaseFilter {
      |  def filter(entity: ModelEntity): Boolean = {
      |    true
      |  }
      |}
    """.stripMargin.trim
  }

  private def createFilter(filter: Filter, file: File): Future[Filter] = {
    val files = Map(file.id -> file.name)
    val entity = filter.copy(files = files)
    repo.create(entity)
  }
}
