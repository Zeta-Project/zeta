package models.frontend

import scala.collection.immutable.Queue
import julienrf.json.derived
import models.worker.Job
import play.api.libs.json.__
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Writes

/**
 * Request messages which can be send by a Tool-Developer
 */
sealed trait DeveloperRequest extends Request
case class RunGenerator(generator: String, filter: String) extends DeveloperRequest
case class RunFilter(filter: String) extends DeveloperRequest
case class CreateGenerator(image: String, options: String) extends DeveloperRequest
case class RunModelRelease(model: String) extends DeveloperRequest
case class CancelWorkByUser(id: String) extends DeveloperRequest

object DeveloperRequest {
  implicit val format: OFormat[DeveloperRequest] = derived.flat.oformat((__ \ "action").format[String])
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
  override def toString(): String = messages.map(log => log.message).mkString
}

object DeveloperResponse {
  implicit val writeGeneratorImageNotFoundFailure = Json.writes[GeneratorImageNotFoundFailure]
  implicit val writeGeneratorNotFoundFailure = Json.writes[ExecuteGeneratorError]
  implicit val writeFilterNotFoundFailure = Json.writes[ExecuteFilterError]
  implicit val writeServiceUnavailable = Json.writes[ServiceUnavailable]
  implicit val writeJobInfo = Json.writes[JobInfo]
  implicit val writeJobInfoList = Json.writes[JobInfoList]
  implicit val writeJobLogMessage = Json.writes[JobLogMessage]
  implicit val writeJobLog = Json.writes[JobLog]

  implicit val write = new Writes[DeveloperResponse] {
    override def writes(response: DeveloperResponse): JsObject = response match {
      case s: GeneratorImageNotFoundFailure => Json.toJson(s).as[JsObject] + ("type", JsString("Error"))
      case s: ExecuteGeneratorError => Json.toJson(s).as[JsObject] + ("type", JsString("Error"))
      case s: ExecuteFilterError => Json.toJson(s).as[JsObject] + ("type", JsString("Error"))
      case s: ServiceUnavailable => Json.toJson(s).as[JsObject] + ("type", JsString("Error"))
      case s: JobInfo => Json.toJson(s).as[JsObject] + ("type", JsString("JobInfo"))
      case s: JobInfoList => Json.toJson(s).as[JsObject] + ("type", JsString("JobInfoList"))
      case s: JobLogMessage => Json.toJson(s).as[JsObject] + ("type", JsString("JobLogMessage"))
      case s: JobLog => Json.toJson(s).as[JsObject] + ("type", JsString("JobLog"))
    }
  }
}

