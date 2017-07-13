package de.htwg.zeta.common.models.frontend

import java.util.UUID

import grizzled.slf4j.Logging
import play.api.libs.json.Format
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue

sealed trait GeneratorRequest extends Request

case class RunGeneratorFromGenerator(parent: String, key: String, generatorId: UUID, options: String) extends GeneratorRequest

case class ToGenerator(index: Int, key: String, receiver: String, message: String) extends GeneratorRequest

object GeneratorRequest extends Logging {

  implicit val format: Format[GeneratorRequest] = new Format[GeneratorRequest] {

    override def writes(o: GeneratorRequest): JsValue = {
      o match {
        case o: RunGeneratorFromGenerator => writeRunGeneratorFromGenerator(o)
        case o: ToGenerator => writeToGenerator(o)
      }
    }

    private def writeRunGeneratorFromGenerator(o: RunGeneratorFromGenerator): JsObject = {
      Json.obj(
        "action" -> "RunGeneratorFromGenerator",
        "parent" -> o.parent,
        "key" -> o.key,
        "generator" -> o.generatorId,
        "options" -> o.options
      )
    }

    private def writeToGenerator(o: ToGenerator): JsObject = {
      Json.obj(
        "action" -> "RunGeneratorFromGenerator",
        "index" -> o.index,
        "key" -> o.key,
        "receiver" -> o.receiver,
        "message" -> o.message
      )
    }


    override def reads(json: JsValue): JsResult[GeneratorRequest] = {
      json.\("action").validate[String].flatMap {
        case "RunGeneratorFromGenerator" => readRunGeneratorFromGenerator(json)
        case "ToGenerator" => readToGenerator(json)
      }
    }

    private def readRunGeneratorFromGenerator(json: JsValue): JsResult[GeneratorRequest] = {
      for {
        parent <- json.\("parent").validate[String]
        key <- json.\("key").validate[String]
        generatorId <- json.\("generator").validate[UUID]
        options <- json.\("options").validate[String]
      } yield {
        RunGeneratorFromGenerator(parent, key, generatorId, options)
      }
    }

    private def readToGenerator(json: JsValue): JsResult[ToGenerator] = {
      for {
        index <- json.\("index").validate[Int]
        key <- json.\("key").validate[String]
        receiver <- json.\("receiver").validate[String]
        message <- json.\("message").validate[String]
      } yield {
        ToGenerator(index, key, receiver, message)
      }
    }

  }

}

sealed trait GeneratorResponse extends Response

case class StartGeneratorError(key: String, reason: String) extends GeneratorResponse

case class FromGenerator(index: Int, key: String, message: String) extends GeneratorResponse

case class GeneratorCompleted(key: String, result: Int) extends GeneratorResponse


object StartGeneratorError {

  implicit val format: Format[StartGeneratorError] = new Format[StartGeneratorError] {

    override def writes(o: StartGeneratorError): JsValue = {
      Json.obj(
        "type" -> "StartGeneratorError",
        "key" -> o.key,
        "reason" -> o.reason
      )
    }

    override def reads(json: JsValue): JsResult[StartGeneratorError] = {
      for {
        key <- json.\("key").validate[String]
        reason <- json.\("reason").validate[String]
      } yield {
        StartGeneratorError(key, reason)
      }
    }

  }

}

object FromGenerator {

  implicit val format: Format[FromGenerator] = new Format[FromGenerator] {

    override def writes(o: FromGenerator): JsValue = {
      Json.obj(
        "type" -> "FromGenerator",
        "index" -> o.index,
        "key" -> o.key,
        "message" -> o.message
      )
    }

    override def reads(json: JsValue): JsResult[FromGenerator] = {
      for {
        index <- json.\("index").validate[Int]
        key <- json.\("key").validate[String]
        message <- json.\("message").validate[String]
      } yield {
        FromGenerator(index, key, message)
      }
    }

  }

}

object GeneratorCompleted {

  implicit val format: Format[GeneratorCompleted] = new Format[GeneratorCompleted] {

    override def writes(o: GeneratorCompleted): JsValue = {
      Json.obj(
        "type" -> "FromGenerator",
        "key" -> o.key,
        "result" -> o.result
      )
    }

    override def reads(json: JsValue): JsResult[GeneratorCompleted] = {
      for {
        key <- json.\("key").validate[String]
        result <- json.\("result").validate[Int]
      } yield {
        GeneratorCompleted(key, result)
      }
    }

  }

}

object GeneratorResponse {

  implicit val format: Format[GeneratorResponse] = new Format[GeneratorResponse] {

    override def writes(o: GeneratorResponse): JsValue = {
      o match {
        case o: StartGeneratorError => StartGeneratorError.format.writes(o)
        case o: FromGenerator => FromGenerator.format.writes(o)
        case o: GeneratorCompleted => GeneratorCompleted.format.writes(o)
      }
    }

    override def reads(json: JsValue): JsResult[GeneratorResponse] = {
      json.\("action").validate[String].flatMap {
        case "StartGeneratorError" => StartGeneratorError.format.reads(json)
        case "FromGenerator" => FromGenerator.format.reads(json)
        case "GeneratorCompleted" => GeneratorCompleted.format.reads(json)
      }
    }

  }

}
