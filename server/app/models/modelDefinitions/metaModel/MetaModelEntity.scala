package models.modelDefinitions.metaModel

import java.time.Instant

import models.modelDefinitions.helper.HLink
import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class MetaModelEntity(
  id: String,
  userId: String,
  created: Instant,
  updated: Instant,
  metaModel: MetaModel,
  dsl: Dsl,
  links: Option[Seq[HLink]] = None
) {

  def asNew(userId: String) = {
    val now = Instant.now
    copy(
      id = java.util.UUID.randomUUID().toString,
      userId = userId,
      created = now,
      updated = now
    )
  }

  def asUpdate(id: String, userId: String) = {
    copy(
      id = id,
      userId = userId,
      updated = Instant.now
    )
  }

}

object MetaModelEntity {

  implicit val reads = Json.reads[MetaModelEntity]

  implicit val writes: OWrites[MetaModelEntity] = (
    (__ \ "id").write[String] and
      (__ \ "userId").write[String] and
      (__ \ "created").write[Instant] and
      (__ \ "updated").write[Instant] and
      (__ \ "metaModel").write[MetaModel] and
      (__ \ "dsl").write[Dsl] and
      (__ \ "links").writeNullable[Seq[HLink]]
    ) (unlift(MetaModelEntity.unapply))


  val strippedReads: Reads[MetaModelEntity] = (
    Reads.pure("") and
      Reads.pure("") and
      Reads.pure(Instant.now()) and
      Reads.pure(Instant.now()) and
      (__ \ "metaModel").read[MetaModel] and
      Reads.pure(Dsl()) and
      Reads.pure(None)
    ) (MetaModelEntity.apply _)

  val strippedWrites = new Writes[MetaModelEntity] {
    override def writes(m: MetaModelEntity): JsValue = Json.obj(
      "id" -> m.id,
      "created" -> m.created,
      "updated" -> m.updated,
      "metaModel" -> m.metaModel,
      "dsl" -> m.dsl,
      "links" -> m.links
    )
  }
}

case class MetaModelShortInfo(
  id: String,
  name: String,
  created: Instant,
  updated: Instant,
  links: Option[Seq[HLink]] = None
)

object MetaModelShortInfo {

  implicit val reads: Reads[MetaModelShortInfo] = (
    (__ \ "id").read[String] and
      (__ \ "metaModel" \ "name").read[String] and
      (__ \ "created").read[Instant] and
      (__ \ "updated").read[Instant] and
      (__ \ "links").readNullable[Seq[HLink]]
    )(MetaModelShortInfo.apply _)

  implicit val writes = Json.writes[MetaModelShortInfo]
}


