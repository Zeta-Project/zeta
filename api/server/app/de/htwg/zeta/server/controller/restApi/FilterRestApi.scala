package de.htwg.zeta.server.controller.restApi

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.format.entity.FilterFormat
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.persistence.general.FileRepository
import de.htwg.zeta.persistence.general.FilterRepository
import de.htwg.zeta.server.silhouette.ZetaEnv
import grizzled.slf4j.Logging
import play.api.libs.json.JsArray
import play.api.libs.json.JsError
import play.api.libs.json.JsPath
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.JsonValidationError
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result
import play.api.mvc.Results


/**
 * REST-ful API for filter definitions
 */
class FilterRestApi @Inject()(
    filterRepo: FilterRepository,
    fileRepo: FileRepository,
    filterFormat: FilterFormat
) extends Controller with Logging {

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
    filterRepo.readAllIds().flatMap(ids => {
      val list = ids.toList.map(filterRepo.read)
      Future.sequence(list)
    })
  }

  private def getJsonArray(list: List[Filter]) = {
    val entities = list.filter(e => !e.deleted)
    val entries = entities.map(filterFormat.writes)
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
    filterRepo.read(id).flatMap(entity => {
      Future(Ok(filterFormat.writes(entity)))
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
    filterRepo.update(id, e => e.copy(deleted = true))
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
    json.validate(filterFormat) match {
      case s: JsSuccess[Filter] => Future.successful(s)
      case e: JsError => Future.successful(e)
    }
  }

  private def jsErrorToResult(errors: Seq[(JsPath, Seq[JsonValidationError])]): Future[Result] = {
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
    fileRepo.create(file)
  }

  private def fileTemplate(): String = {
    s"""
      |class Filter() extends BaseFilter {
      |  def filter(entity: GraphicalDslInstance): Boolean = {
      |    true
      |  }
      |}
    """.stripMargin.trim
  }

  private def createFilter(filter: Filter, file: File): Future[Filter] = {
    val files = Map(file.id -> file.name)
    val entity = filter.copy(files = files)
    filterRepo.create(entity)
  }
}
