package de.htwg.zeta.server.controller.restApi.modelUiFormat

import java.util.UUID

import scala.collection.immutable.List
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes



class ModelEntityFormat(userID: UUID) extends Reads[Future[JsResult[ModelEntity]]] with Writes[ModelEntity] {
  val modelFormat = ModelFormat(userID)

  override def reads(json: JsValue): JsResult[Future[JsResult[ModelEntity]]] = {
    for {
      id <- (json \ "id").validate[UUID]
      metaModelId <- (json \ "metaModelId").validate[UUID]
      model: Future[JsResult[Model]] <- (json \ "model").validate(modelFormat)
    } yield {
      model.map(_.map(model => {
        ModelEntity(id, model)
      }))
    }
  }

  override def writes(o: ModelEntity): JsValue = ModelEntityFormat.writes(o)
}

object ModelEntityFormat extends Writes[ModelEntity] {

  def apply(userID: UUID): ModelEntityFormat = new ModelEntityFormat(userID)

  override def writes(o: ModelEntity): JsValue = {
    JsObject(List(
      "id" -> Json.toJson(o.id),
      "metaModelId" -> Json.toJson(o.model.metaModelId),
      "model" -> ModelFormat.writes(o.model)
    ))
  }
}
