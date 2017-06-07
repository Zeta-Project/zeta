package models.modelDefinitions.model

import java.time.Instant
import java.util.UUID

import models.modelDefinitions.helper.HLink
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.ModelElement
import models.modelDefinitions.model.elements.ModelReads
import play.api.libs.functional.syntax
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.__
import play.api.libs.json.OWrites
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import play.api.libs.json.__

/**
 * The container that is used to persist model definitions, contains additional metadata
 * @param id the id of the model
 * @param metaModelId the id of the corresponding metamodel
 * @param userId the user that created the model
 * @param created time of creation
 * @param updated time of last update
 * @param model the actual model
 * @param links optional attribute for HATEOAS links, only used when serving to clients
 */
case class ModelEntity(id: UUID, metaModelId: UUID, userId: UUID, created: Instant, updated: Instant, model: Model, links: Option[Seq[HLink]] = None) {
  // sets initial data for insert
  def asNew(userId: UUID, metaModelId: UUID): ModelEntity = {
    val now = Instant.now
    copy(id = UUID.randomUUID, metaModelId = metaModelId, userId = userId, created = now, updated = now)
  }
  // overrides unchanging data for update
  def asUpdate(id: UUID): ModelEntity = {
    copy(
      id = id,
      updated = Instant.now
    )
  }

}

object ModelEntity {

  def reads(implicit meta: MetaModel): Reads[Model] = {
    val mapReads = ModelReads.elementMapReads(meta)
    ((__ \ "name").read[String] and
      Reads.pure(meta) and
      (__ \ "elements").read[Map[String, ModelElement]](mapReads) and
      (__ \ "uiState").read[String])(Model.apply _)
  }

  def reads(metaModelId: UUID, metaModel: MetaModel): Reads[ModelEntity] = (
    (__ \ "id").read[UUID] and
    Reads.pure(metaModelId) and
    (__ \ "userId").read[UUID] and
    (__ \ "created").read[Instant] and
    (__ \ "updated").read[Instant] and
    (__ \ "model").read[Model](ModelEntity.reads(metaModel)) and
    (__ \ "links").readNullable[Seq[HLink]]
  )(ModelEntity.apply _)

  implicit val writes: OWrites[ModelEntity] = (
    (__ \ "id").write[UUID] and
    (__ \ "metaModelId").write[UUID] and
    (__ \ "userId").write[UUID] and
    (__ \ "created").write[Instant] and
    (__ \ "updated").write[Instant] and
    (__ \ "model").write[Model] and
    (__ \ "links").writeNullable[Seq[HLink]]
  )(syntax.unlift(ModelEntity.unapply))

  /** stripped reads/writes are used when communicating with clients */
  def strippedReads(metaModelId: UUID, metaModel: MetaModel): Reads[ModelEntity] = (
    Reads.pure(UUID.randomUUID) and
    Reads.pure(metaModelId) and
    Reads.pure(UUID.randomUUID) and
    Reads.pure(Instant.now()) and
    Reads.pure(Instant.now()) and
    (__ \ "model").read[Model](ModelEntity.reads(metaModel)) and
    Reads.pure(None)
  )(ModelEntity.apply _)

  val strippedWrites = new Writes[ModelEntity] {
    override def writes(m: ModelEntity): JsValue = Json.obj(
      "id" -> m.id,
      "metaModelId" -> m.metaModelId,
      "created" -> m.created,
      "updated" -> m.updated,
      "model" -> m.model,
      "links" -> m.links
    )
  }
}

/**
 * Represents concise information on a model, used for REST-API overview uri
 * @param id the id of the model
 * @param metaModelId the name of the metamodel
 * @param name the name of themodel
 * @param links optional attribute for HATEOAS links, only used when serving to clients
 */
case class ModelShortInfo(id: UUID, metaModelId: UUID, name: String, links: Option[Seq[HLink]] = None)

object ModelShortInfo {

  implicit val reads: Reads[ModelShortInfo] = (
    (__ \ "id").read[UUID] and
    (__ \ "metaModelId").read[UUID] and
    (__ \ "model" \ "name").read[String] and
    (__ \ "links").readNullable[Seq[HLink]]
  )(ModelShortInfo.apply _)

  implicit val writes: OWrites[ModelShortInfo] = Json.writes[ModelShortInfo]
}
