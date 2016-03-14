package models.modelDefinitions.model

import java.time.Instant

import models.modelDefinitions.metaModel.{MetaModelEntity, Dsl, MetaModel}
import play.api.libs.functional.syntax._
import play.api.libs.json._


case class ModelEntity(
  id: String,
  metaModelId: String,
  userId: String,
  created: Instant,
  updated: Instant,
  model: Model
){

  def asNew(userId: String, metaModelId: String) = {
    val now = Instant.now
    copy(
      id = java.util.UUID.randomUUID().toString,
      metaModelId = metaModelId,
      userId = userId,
      created = now,
      updated = now
    )
  }

  def asUpdate(id: String) = {
    copy(
      id = id,
      updated = Instant.now
    )
  }

}

object ModelEntity {

 def reads(metaModelId: String, metaModel: MetaModel): Reads[ModelEntity] = (
    (__ \ "id").read[String] and
      Reads.pure(metaModelId) and
      (__ \ "userId").read[String] and
      (__ \ "created").read[Instant] and
      (__ \ "updated").read[Instant] and
      (__ \ "model").read[Model](Model.reads(metaModel))
    ) (ModelEntity.apply _)

  implicit val writes: OWrites[ModelEntity] = (
    (__ \ "id").write[String] and
      (__ \ "metaModelId").write[String] and
      (__ \ "userId").write[String] and
      (__ \ "created").write[Instant] and
      (__ \ "updated").write[Instant] and
      (__ \ "model").write[Model]
    ) (unlift(ModelEntity.unapply))


  def strippedReads(metaModelId: String, metaModel: MetaModel): Reads[ModelEntity] = (
    Reads.pure("") and
      Reads.pure(metaModelId) and
      Reads.pure("") and
      Reads.pure(Instant.now()) and
      Reads.pure(Instant.now()) and
      (__ \ "model").read[Model](Model.reads(metaModel))
    ) (ModelEntity.apply _)

  val strippedWrites = new Writes[ModelEntity] {
    override def writes(m: ModelEntity): JsValue = Json.obj(
      "id" -> m.id,
      "metaModelId" -> m.metaModelId,
      "created" -> m.created,
      "updated" -> m.updated,
      "model" -> m.model
    )
  }
}

case class ModelShortInfo(id: String, metaModelId: String, name: String, created: Instant, updated: Instant)

object ModelShortInfo {

  implicit val reads: Reads[ModelShortInfo] = (
    (__ \ "id").read[String] and
      (__ \ "metaModelId").read[String] and
      (__ \ "model" \ "name").read[String] and
      (__ \ "created").read[Instant] and
      (__ \ "updated").read[Instant]
    )(ModelShortInfo.apply _)

  implicit val writes = Json.writes[ModelShortInfo]
}