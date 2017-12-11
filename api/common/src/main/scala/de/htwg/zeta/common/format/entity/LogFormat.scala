package de.htwg.zeta.common.format.entity

import java.util.UUID

import de.htwg.zeta.common.models.entity.Log
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class LogFormat(
    sId: String = "id",
    sTask: String = "task",
    sLog: String = "log",
    sStatus: String = "status",
    sDate: String = "date"
) extends OFormat[Log] {

  override def writes(log: Log): JsObject = Json.obj(
    sId -> log.id,
    sTask -> log.task,
    sLog -> log.log,
    sStatus -> log.status,
    sDate -> log.date
  )

  override def reads(json: JsValue): JsResult[Log] = for {
    id <- (json \ sId).validate[UUID]
    task <- (json \ sTask).validate[String]
    log <- (json \ sLog).validate[String]
    status <- (json \ sStatus).validate[Int]
    date <- (json \ sDate).validate[String]
  } yield {
    Log(id, task, log, status, date)
  }

}
