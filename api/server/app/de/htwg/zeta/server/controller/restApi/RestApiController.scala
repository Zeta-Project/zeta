package de.htwg.zeta.server.controller.restApi

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.reflect.runtime.universe
import scala.reflect.runtime.universe.TypeTag

import com.google.inject.Inject
import grizzled.slf4j.Logging
import play.api.libs.json.JsError
import play.api.libs.json.JsPath
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.JsonValidationError
import play.api.mvc.InjectedController
import play.api.mvc.Result

/**
 * Base class with utilities
 */
class RestApiController[T: TypeTag] @Inject()(
    implicit val ec: ExecutionContext
) extends InjectedController  with Logging {

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

  private def jsErrorToResult(errors: Seq[(JsPath, Seq[JsonValidationError])]): Future[Result] = {
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
