package de.htwg.zeta.common.format.model

import java.util.UUID

import de.htwg.zeta.common.models.entity.ModelEntity
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat


object ModelEntityFormat extends OFormat[ModelEntity] {

  val sId = "id"
  val sModel = "model"

  override def writes(entity: ModelEntity): JsObject = Json.obj(
    sId -> entity.id,
    sModel -> ModelFormat.writes(entity.model)
  )

  override def reads(json: JsValue): JsResult[ModelEntity] = {
    for {
      id <- (json \ sId).validate[UUID]
      model <- (json \ sModel).validate(ModelFormat)
    } yield {
      ModelEntity(id, model)
    }
  }

}
