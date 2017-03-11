package models.modelDefinitions.metaModel

import java.time.Instant

import models.modelDefinitions.helper.HLink
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * The container that is used to persist metamodel definitions, contains additional metadata
  * @param id the id of the metamodel
  * @param userId the user that created the metamodel
  * @param created time of creation
  * @param updated time of last update
  * @param metaModel the actual metamodel
  * @param dsl possible dsl definitions
  * @param links optional attribute for HATEOAS links, only used when serving to clients
  */
case class MetaModelEntity(
  id: String,
  userId: String,
  created: Instant,
  updated: Instant,
  metaModel: MetaModel,
  dsl: Dsl,
  links: Option[Seq[HLink]] = None
) {
  // sets initial data for insert
  def asNew(userId: String) = {
    val now = Instant.now
    copy(
      id = java.util.UUID.randomUUID().toString,
      userId = userId,
      created = now,
      updated = now
    )
  }
  // overrides unchanging data for update
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

  /** stripped reads/writes are used when communicating with clients */
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

/**
  * Represents concise information on a metamodel, used for REST-API overview uri
  * @param id the id of the metamodel
  * @param name the name of the metamodel
  * @param created the time of creation
  * @param updated the time of the last update
  * @param links optional attribute for HATEOAS links, only used when serving to clients
  */
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


