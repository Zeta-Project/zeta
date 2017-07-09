package de.htwg.zeta.common.models.frontend

import java.util.UUID

import scala.collection.immutable.Queue

import de.htwg.zeta.common.models.worker.Job
import grizzled.slf4j.Logging
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.Json
import play.api.libs.json.Writes
import play.api.libs.json.Reads
import play.api.libs.json.JsValue
import play.api.libs.json.JsResult

/**
 * Request messages which can be send by a Tool-Developer
 */
sealed trait DeveloperRequest extends Request
case class RunGenerator(generatorId: UUID, filterId: UUID) extends DeveloperRequest
case class RunFilter(filterId: UUID) extends DeveloperRequest
case class CreateGenerator(imageId: UUID, options: String) extends DeveloperRequest
case class RunModelRelease(model: String) extends DeveloperRequest
case class CancelWorkByUser(id: String) extends DeveloperRequest


object DeveloperRequest extends Logging {

  private object DeveloperRequestFormat extends Reads[DeveloperRequest] {

    def readRunGenerator(json: JsValue): JsResult[RunGenerator] = {
      for {
        generatorId <- json.\("generator").validate[UUID]
        filterId <- json.\("filter").validate[UUID]
      } yield {
        RunGenerator(generatorId, filterId)
      }
    }

    def readRunFilter(json: JsValue): JsResult[RunFilter] = {
      for {
        filterId <- json.\("filter").validate[UUID]
      } yield {
        RunFilter(filterId)
      }
    }

    def readCreateGenerator(json: JsValue): JsResult[CreateGenerator] = {
      for {
        imageId <- json.\("image").validate[UUID]
        options <- json.\("options").validate[String]
      } yield {
        CreateGenerator(imageId, options)
      }
    }

    def readRunModelRelease(json: JsValue): JsResult[RunModelRelease] = {
      for {
        model <- json.\("model").validate[String]
      } yield {
        RunModelRelease(model)
      }
    }

    def readCancelWorkByUser(json: JsValue): JsResult[CancelWorkByUser] = {
      for {
        id <- json.\("id").validate[String]
      } yield {
        CancelWorkByUser(id)
      }
    }

    override def reads(json: JsValue): JsResult[DeveloperRequest] = {
      json.\("action").validate[String].flatMap {
        case "RunGenerator" => readRunGenerator(json)
        case "RunFilter" => readRunFilter(json)
        case "CreateGenerator" => readCreateGenerator(json)
        case "RunModelRelease" => readRunModelRelease(json)
        case "CancelWorkByUser" => readCancelWorkByUser(json)
      }
    }
  }

  implicit val reads: Reads[DeveloperRequest] = DeveloperRequestFormat
}

/**
 * Response messages which can be send to a Tool-Developer
 */
sealed trait DeveloperResponse extends Response
case class GeneratorImageNotFoundFailure(message: String = "Generator Image was not found") extends DeveloperResponse
case class ExecuteGeneratorError(message: String = "Generator or Filter was not found") extends DeveloperResponse
case class ExecuteFilterError(message: String = "Filter was not found") extends DeveloperResponse
case class ServiceUnavailable(message: String = "Service not available") extends DeveloperResponse
case class JobInfo(id: String, job: Job, state: String) extends DeveloperResponse
case class JobInfoList(jobs: List[JobInfo], running: Int, pending: Int, waiting: Int, canceled: Int) extends DeveloperResponse
case class JobLogMessage(message: String, sort: String) extends DeveloperResponse
case class JobLog(job: String, messages: Queue[JobLogMessage] = Queue.empty) extends DeveloperResponse {
  override def toString: String = messages.map(log => log.message).mkString
}

object DeveloperResponse {
  implicit val writeGeneratorImageNotFoundFailure: Writes[GeneratorImageNotFoundFailure] = Json.writes[GeneratorImageNotFoundFailure]
  implicit val writeGeneratorNotFoundFailure: Writes[ExecuteGeneratorError] = Json.writes[ExecuteGeneratorError]
  implicit val writeFilterNotFoundFailure: Writes[ExecuteFilterError] = Json.writes[ExecuteFilterError]
  implicit val writeServiceUnavailable: Writes[ServiceUnavailable] = Json.writes[ServiceUnavailable]
  implicit val writeJobInfo: Writes[JobInfo] = Json.writes[JobInfo]
  implicit val writeJobInfoList: Writes[JobInfoList] = Json.writes[JobInfoList]
  implicit val writeJobLogMessage: Writes[JobLogMessage] = Json.writes[JobLogMessage]
  implicit val writeJobLog: Writes[JobLog] = Json.writes[JobLog]

  implicit val write = new Writes[DeveloperResponse] {
    override def writes(response: DeveloperResponse): JsObject = response match {
      case s: GeneratorImageNotFoundFailure => Json.toJson(s).as[JsObject] + (("type", JsString("Error")))
      case s: ExecuteGeneratorError => Json.toJson(s).as[JsObject] + (("type", JsString("Error")))
      case s: ExecuteFilterError => Json.toJson(s).as[JsObject] + (("type", JsString("Error")))
      case s: ServiceUnavailable => Json.toJson(s).as[JsObject] + (("type", JsString("Error")))
      case s: JobInfo => Json.toJson(s).as[JsObject] + (("type", JsString("JobInfo")))
      case s: JobInfoList => Json.toJson(s).as[JsObject] + (("type", JsString("JobInfoList")))
      case s: JobLogMessage => Json.toJson(s).as[JsObject] + (("type", JsString("JobLogMessage")))
      case s: JobLog => Json.toJson(s).as[JsObject] + (("type", JsString("JobLog")))
    }
  }
}

