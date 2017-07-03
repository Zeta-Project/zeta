package de.htwg.zeta.server.controller.restApi.modelUiFormat

import java.util.UUID

import scala.collection.immutable.Seq
import scala.collection.immutable.List
import scala.concurrent.Future

import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.modelDefinitions.helper.HLink
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import play.api.libs.json.Json
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.Reads
import play.api.libs.json.JsValue
import play.api.libs.json.Writes
import scala.concurrent.ExecutionContext.Implicits.global



class ModelEntityFormat(userID: UUID) extends Reads[Future[JsResult[ModelEntity]]] with Writes[ModelEntity] {
  val modelFormat = ModelFormat(userID)

  override def reads(json: JsValue): JsResult[Future[JsResult[ModelEntity]]] = {
    for {
      id <- (json \ "id").validate[UUID]
      metaModelId <- (json \ "metaModelId").validate[UUID]
      model: Future[JsResult[Model]] <- (json \ "model").validate(modelFormat)
      links <- (json \ "links").validateOpt[Seq[HLink]]
    } yield {
      model.map(_.map(model => {
        ModelEntity(id, model, metaModelId, links)
      }))
    }
  }

  override def writes(o: ModelEntity): JsValue = ModelEntityFormat.writes(o)
}

object ModelEntityFormat extends Writes[ModelEntity] {

  def apply(userID: UUID): ModelEntityFormat = new ModelEntityFormat(userID)

  override def writes(o: ModelEntity): JsValue = {
    val list = List(
      "id" -> Json.toJson(o.id),
      "metaModelId" -> Json.toJson(o.metaModelId),
      "model" -> ModelFormat.writes(o.model)
    )
    val complete =
      o.links match {
        case Some(links) => ("links", Json.toJson(links)) :: list
        case None => list
      }
    JsObject(complete)
  }
}
