package de.htwg.zeta.common.format.model

import java.util.UUID

import de.htwg.zeta.common.models.entity.ModelEntity
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat


class ModelEntityFormat(
    modelFormat: ModelFormat,
    sId: String = "id",
    sModel: String = "model"
) extends OFormat[ModelEntity] {

  override def writes(entity: ModelEntity): JsObject = Json.obj(
    sId -> entity.id,
    sModel -> modelFormat.writes(entity.model)
  )

  override def reads(json: JsValue): JsResult[ModelEntity] = for {
    id <- (json \ sId).validate[UUID]
    model <- (json \ sModel).validate(modelFormat)
  } yield {
    ModelEntity(id, model)
  }

}
