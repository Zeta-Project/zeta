package de.htwg.zeta.common.models.frontend

import java.util.UUID

import scala.collection.immutable.Queue

import de.htwg.zeta.common.models.worker.Job
import grizzled.slf4j.Logging
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.Writes

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
sealed trait ErrorResponse extends DeveloperResponse { def message: String }
case class GeneratorImageNotFoundFailure(message: String = "Generator Image was not found") extends ErrorResponse
case class ExecuteGeneratorError(message: String = "Generator or Filter was not found") extends ErrorResponse
case class ExecuteFilterError(message: String = "Filter was not found") extends ErrorResponse
case class ServiceUnavailable(message: String = "Service not available") extends ErrorResponse
case class JobInfo(id: String, job: Job, state: String) extends DeveloperResponse
case class JobInfoList(jobs: List[JobInfo], running: Int, pending: Int, waiting: Int, canceled: Int) extends DeveloperResponse
case class JobLogMessage(message: String, sort: String) extends DeveloperResponse
case class JobLog(job: String, messages: Queue[JobLogMessage] = Queue.empty) extends DeveloperResponse {
  override def toString: String = messages.map(log => log.message).mkString
}

object DeveloperResponse extends Logging {
  private val attribute = "type"

  implicit val write = new Writes[DeveloperResponse] {
    override def writes(response: DeveloperResponse): JsObject = {
      try {
        processWrites(response)
      } catch {
        case e: Throwable =>
          error("Failed parse jsob")
          createErrorResponse("Unknown parse error")
      }
    }

    private def processWrites(response: DeveloperResponse): JsObject = {
      response match {
        case s: ErrorResponse => createErrorResponse(s.message)
        case s: JobInfo => createJobInfo(s) + ((attribute, JsString("JobInfo")))
        case s: JobInfoList => createJobInfoList(s) + ((attribute, JsString("JobInfoList")))
        case s: JobLogMessage => createJobLogMessage(s) + ((attribute, JsString("JobLogMessage")))
        case s: JobLog => createJobLog(s) + ((attribute, JsString("JobLog")))
        case _ =>
          error("Unknown DeveloperResponse: " + response.toString)
          createErrorResponse("Unknown object to parse")
      }
    }

    private def createErrorResponse(message: String): JsObject = {
      Json.obj(
        attribute -> "Error",
        "message" -> message
      )
    }

    private def createJobInfoList(o: JobInfoList): JsObject = {
      Json.obj(
        "jobs" -> JsArray(o.jobs.map(createJobInfo)),
        "running" -> o.running,
        "pending" -> o.pending,
        "waiting" -> o.waiting,
        "canceled" -> o.canceled
      )
    }

    private def createJobInfo(o: JobInfo): JsObject = {
      Json.obj(
        "id" -> o.id,
        "job" -> o.job,
        "state" -> o.state
      )
    }

    private def createJobLogMessage(o: JobLogMessage): JsObject = {
      Json.obj(
        "message" -> o.message,
        "sort" -> o.sort
      )
    }

    private def createJobLog(o: JobLog): JsObject = {
      Json.obj(
        "job" -> o.job,
        "messages" -> JsArray(o.messages.map(createJobLogMessage))
      )
    }
  }
}

