package models.metaModel

import java.time.Instant

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class MetaModelEntity(
  id: String,
  userId: String,
  created: Instant,
  updated: Instant,
  definition: MetaModel
)

object MetaModelEntity {

  def initialize(userId: String, metaModel: MetaModel) = {
    val now = Instant.now
    MetaModelEntity(
      java.util.UUID.randomUUID().toString,
      userId,
      now,
      now,
      metaModel
    )
  }

  implicit val metaModelReads = Json.reads[MetaModelEntity]

  implicit val metaModelWrites: OWrites[MetaModelEntity] = (
    (__ \ "id").write[String] and
      (__ \ "userId").write[String] and
      (__ \ "created").write[Instant] and
      (__ \ "updated").write[Instant] and
      (__ \ "definition").write[MetaModel]
    ) (unlift(MetaModelEntity.unapply))


  val strippedReads: Reads[MetaModelEntity] = (
    Reads.pure("") and
      Reads.pure("") and
      Reads.pure(Instant.now()) and
      Reads.pure(Instant.now()) and
      (__ \ "definition").read[MetaModel]
    ) (MetaModelEntity.apply _)

  val strippedWrites = new Writes[MetaModelEntity] {
    override def writes(m: MetaModelEntity): JsValue = Json.obj(
      "id" -> m.id,
      "created" -> m.created,
      "updated" -> m.updated,
      "definition" -> m.definition
    )
  }
}

case class MetaModelShortInfo(id: String, name: String, created: Instant, updated: Instant)

object MetaModelShortInfo {
  implicit val shortInfoWrites = Json.writes[MetaModelShortInfo]
}