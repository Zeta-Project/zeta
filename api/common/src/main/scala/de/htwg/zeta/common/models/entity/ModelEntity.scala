package de.htwg.zeta.common.models.entity

import java.util.UUID

import de.htwg.zeta.common.models.modelDefinitions.helper.HLink
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import play.api.libs.json.Format
import play.api.libs.json.Json

// TODO remove metaModelId as it is already defined in Model#metaModelId
case class ModelEntity(
    id: UUID,
    model: Model,
    metaModelId: UUID,
    links: Option[Seq[HLink]] = None
) extends Entity

object ModelEntity {

  implicit val playJsonModelEntityFormat: Format[ModelEntity] = Json.format[ModelEntity]

}
