package models.entity

import java.util.UUID

import models.modelDefinitions.helper.HLink
import models.modelDefinitions.model.Model
import play.api.libs.json.Format
import play.api.libs.json.Json


case class ModelEntity(
    id: UUID = UUID.randomUUID,
    model: Model,
    metaModelId: UUID,
    links: Option[Seq[HLink]] = None
) extends Entity

object ModelEntity {

  implicit val playJsonModelEntityFormat: Format[ModelEntity] = Json.format[ModelEntity]

}
