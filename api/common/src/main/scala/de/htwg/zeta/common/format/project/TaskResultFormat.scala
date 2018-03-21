package de.htwg.zeta.common.format.project

import de.htwg.zeta.common.models.project.TaskResult
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads

class TaskResultFormat(
    sErrorDsl: String,
    sMessages: String,
    sSuccess: String
) extends OFormat[TaskResult] {

  override def writes(clazz: TaskResult): JsObject = Json.obj(
    sErrorDsl -> clazz.errorDsl,
    sMessages -> clazz.messages,
    sSuccess -> clazz.success
  )

  override def reads(json: JsValue): JsResult[TaskResult] = for {
    errorDsl <- (json \ sErrorDsl).validate[String]
    messages <- (json \ sMessages).validate(Reads.list[String])
    success <- (json \ sSuccess).validate[Boolean]
  } yield {
    TaskResult(errorDsl, messages, success)
  }

}
object TaskResultFormat {
  def apply(): TaskResultFormat = new TaskResultFormat("errorDsl", "messages", "success")
}
