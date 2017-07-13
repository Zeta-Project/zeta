package de.htwg.zeta.common.models.frontend

import java.util.UUID

import de.htwg.zeta.common.models.frontend.SafeFormats.safeRead
import de.htwg.zeta.common.models.frontend.SafeFormats.safeWrite
import grizzled.slf4j.Logging
import play.api.libs.json.Format
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json

sealed trait GeneratorRequest extends Request

case class RunGeneratorFromGenerator(parent: String, key: String, generatorId: UUID, options: String) extends GeneratorRequest

case class ToGenerator(index: Int, key: String, receiver: String, message: String) extends GeneratorRequest

object GeneratorRequest extends Logging {

  implicit val format: Format[GeneratorRequest] = new Format[GeneratorRequest] {

    override def writes(o: GeneratorRequest): JsValue = safeWrite{
      o match {
        case o: RunGeneratorFromGenerator => writeRunGeneratorFromGenerator(o)
        case o: ToGenerator => writeToGenerator(o)
      }
    }

    private def writeRunGeneratorFromGenerator(o: RunGeneratorFromGenerator): JsValue = safeWrite{
      Json.obj(
        "action" -> "RunGeneratorFromGenerator",
        "parent" -> o.parent,
        "key" -> o.key,
        "generator" -> o.generatorId,
        "options" -> o.options
      )
    }

    private def writeToGenerator(o: ToGenerator): JsValue = safeWrite {
      Json.obj(
        "action" -> "RunGeneratorFromGenerator",
        "index" -> o.index,
        "key" -> o.key,
        "receiver" -> o.receiver,
        "message" -> o.message
      )
    }


    override def reads(json: JsValue): JsResult[GeneratorRequest] = safeRead {
      json.\("action").validate[String].flatMap {
        case "RunGeneratorFromGenerator" => readRunGeneratorFromGenerator(json)
        case "ToGenerator" => readToGenerator(json)
      }
    }

    private def readRunGeneratorFromGenerator(json: JsValue): JsResult[GeneratorRequest] = safeRead {
      for {
        parent <- json.\("parent").validate[String]
        key <- json.\("key").validate[String]
        generatorId <- json.\("generator").validate[UUID]
        options <- json.\("options").validate[String]
      } yield {
        RunGeneratorFromGenerator(parent, key, generatorId, options)
      }
    }

    private def readToGenerator(json: JsValue): JsResult[ToGenerator] = safeRead {
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

    override def writes(o: StartGeneratorError): JsValue = safeWrite {
      Json.obj(
        "type" -> "StartGeneratorError",
        "key" -> o.key,
        "reason" -> o.reason
      )
    }

    override def reads(json: JsValue): JsResult[StartGeneratorError] = safeRead {
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

    override def writes(o: FromGenerator): JsValue = safeWrite {
      Json.obj(
        "type" -> "FromGenerator",
        "index" -> o.index,
        "key" -> o.key,
        "message" -> o.message
      )
    }

    override def reads(json: JsValue): JsResult[FromGenerator] = safeRead {
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
      safeWrite {
        Json.obj(
          "type" -> "FromGenerator",
          "key" -> o.key,
          "result" -> o.result
        )
      }
    }

    override def reads(json: JsValue): JsResult[GeneratorCompleted] = {
      safeRead {
        for {
          key <- json.\("key").validate[String]
          result <- json.\("result").validate[Int]
        } yield {
          GeneratorCompleted(key, result)
        }
      }
    }

  }

}

object GeneratorResponse {

  implicit val format: Format[GeneratorResponse] = new Format[GeneratorResponse] {

    override def writes(o: GeneratorResponse): JsValue = {
      safeWrite {
        o match {
          case o: StartGeneratorError => StartGeneratorError.format.writes(o)
          case o: FromGenerator => FromGenerator.format.writes(o)
          case o: GeneratorCompleted => GeneratorCompleted.format.writes(o)
        }
      }
    }

    override def reads(json: JsValue): JsResult[GeneratorResponse] = {
      safeRead {
        json.\("action").validate[String].flatMap {
          case "StartGeneratorError" => StartGeneratorError.format.reads(json)
          case "FromGenerator" => FromGenerator.format.reads(json)
          case "GeneratorCompleted" => GeneratorCompleted.format.reads(json)
        }
      }
    }

  }


}
