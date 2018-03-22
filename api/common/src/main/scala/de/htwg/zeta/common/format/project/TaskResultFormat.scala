package de.htwg.zeta.common.format.project

import de.htwg.zeta.common.models.project.TaskResult
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads

class TaskResultFormat(
    sMessages: String,
    sSuccess: String
) extends OFormat[TaskResult] {

  override def writes(clazz: TaskResult): JsObject = Json.obj(
    sMessages -> clazz.messages,
    sSuccess -> clazz.success
  )

  override def reads(json: JsValue): JsResult[TaskResult] = for {
    messages <- (json \ sMessages).validate(Reads.list[String])
    success <- (json \ sSuccess).validate[Boolean]
  } yield {
    TaskResult(messages, success)
  }

}
object TaskResultFormat {
  def apply(): TaskResultFormat = new TaskResultFormat("messages", "success")
}
