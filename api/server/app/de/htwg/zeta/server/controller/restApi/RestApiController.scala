package de.htwg.zeta.server.controller.restApi

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.runtime.universe
import scala.reflect.runtime.universe.TypeTag

import grizzled.slf4j.Logging
import play.api.data.validation.ValidationError
import play.api.libs.json.JsError
import play.api.libs.json.JsPath
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.mvc.Controller
import play.api.mvc.Result

/**
 * Base class with utilities
 */
class RestApiController[T: TypeTag] extends Controller with Logging {

  protected def parseJson(json: JsValue, reader: Reads[T], callback: (T) => Future[Result]): Future[Result] = {
    val jsResult = json.validate(reader) match {
      case s: JsSuccess[T] => Future.successful(s)
      case e: JsError => Future.successful(e)
    }
    jsResult.flatMap(result => {
      result.fold(
        errors => jsErrorToResult(errors),
        entity => processEntity(entity, callback)
      )
    })
  }

  private def jsErrorToResult(errors: Seq[(JsPath, Seq[ValidationError])]): Future[Result] = {
    val json = JsError.toJson(errors)
    val result = BadRequest(json)
    Future.successful(result)
  }

  private def processEntity(entity: T, callback: (T) => Future[Result]): Future[Result] = {
    callback(entity).recover {
      case e: Exception =>
        error("Exception while trying to insert a `" + universe.typeOf[T].toString + "` into DB", e)
        BadRequest(e.getMessage)
    }
  }
}
