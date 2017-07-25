package de.htwg.zeta.common.models.entity

import java.util.UUID

import de.htwg.zeta.common.models.modelDefinitions.model.Model
import play.api.libs.json.Format
import play.api.libs.json.Json

case class ModelEntity(
    id: UUID,
    model: Model
) extends Entity

object ModelEntity {

  implicit val playJsonModelEntityFormat: Format[ModelEntity] = Json.format[ModelEntity]

}
