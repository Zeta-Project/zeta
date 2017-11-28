package de.htwg.zeta.common.format.entity

import java.util.UUID

import de.htwg.zeta.common.models.entity.TimedTask
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat

/**
 * Parse JsValue to TimedTask and TimedTask to JsValue
 */
object TimedTaskFormat extends OFormat[TimedTask] {

  val attributeId = "id"
  val attributeName = "name"
  val attributeGenerator = "generatorId"
  val attributeFilter = "filterId"
  val attributeInterval = "interval"
  val attributeStart = "start"

  override def writes(o: TimedTask): JsObject = Json.obj(
    attributeId -> o.id.toString,
    attributeName -> o.name,
    attributeGenerator -> o.generatorId,
    attributeFilter -> o.filterId,
    attributeInterval -> o.interval,
    attributeStart -> o.start
  )

  override def reads(json: JsValue): JsResult[TimedTask] = {
    for {
      id <- (json \ attributeId).validateOpt[UUID]
      name <- (json \ attributeName).validate[String]
      generator <- (json \ attributeGenerator).validate[UUID]
      filter <- (json \ attributeFilter).validate[UUID]
      interval <- (json \ attributeInterval).validate[Int]
      start <- (json \ attributeStart).validate[String]
    } yield {
      TimedTask(id.getOrElse(UUID.randomUUID()), name, generator, filter, interval, start)
    }
  }

}
