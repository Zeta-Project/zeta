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
    sSuccess: String,
    sPosition: String,
    sPositionLine: String,
    sPositionCol: String,
    defaultPosition: (Int, Int)
) extends OFormat[TaskResult] {

  override def writes(clazz: TaskResult): JsObject = Json.obj(
    sErrorDsl -> clazz.errorDsl,
    sMessages -> clazz.messages,
    sSuccess -> clazz.success,
    sPosition -> Json.obj(
      sPositionLine -> clazz.position.getOrElse(defaultPosition)._1,
      sPositionCol -> clazz.position.getOrElse(defaultPosition)._2
    )
  )

  override def reads(json: JsValue): JsResult[TaskResult] = for {
    errorDsl <- (json \ sErrorDsl).validate[String]
    messages <- (json \ sMessages).validate(Reads.list[String])
    success <- (json \ sSuccess).validate[Boolean]
    position <- (json \ sPosition).validate[(Int, Int)]
  } yield {
    TaskResult(errorDsl, messages, Some(position), success)
  }

}
object TaskResultFormat {
  def apply(): TaskResultFormat = new TaskResultFormat(
    "errorDsl",
    "messages",
    "success",
    "position",
    "line",
    "column",
    (-1, -1)
  )
}
