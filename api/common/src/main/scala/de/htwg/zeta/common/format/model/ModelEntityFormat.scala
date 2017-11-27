package de.htwg.zeta.common.format.model

import java.util.UUID

import de.htwg.zeta.common.format.model.ModelEntityFormat.sId
import de.htwg.zeta.common.format.model.ModelEntityFormat.sModel
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OWrites
import play.api.libs.json.Reads


object ModelEntityFormat extends OWrites[ModelEntity] {

  val sId = "id"
  val sModel = "model"

  override def writes(entity: ModelEntity): JsObject = Json.obj(
    sId -> entity.id,
    sModel -> ModelFormat.writes(entity.model)
  )

}

class ModelEntityFormat(metaModelId: UUID, metaModel: MetaModel) extends Reads[ModelEntity] {

  override def reads(json: JsValue): JsResult[ModelEntity] = {
    for {
      id <- (json \ sId).validate[UUID]
      model <- (json \ sModel).validate(new ModelFormat(metaModelId, metaModel))
    } yield {
      ModelEntity(id, model)
    }
  }

}
