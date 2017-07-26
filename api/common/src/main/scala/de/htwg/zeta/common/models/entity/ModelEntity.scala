package de.htwg.zeta.common.models.entity

import java.util.UUID

import de.htwg.zeta.common.models.modelDefinitions.model.Model
import play.api.libs.json.Json
import play.api.libs.json.Writes

case class ModelEntity(
    id: UUID,
    model: Model
) extends Entity

object ModelEntity {

  implicit val playJsonWrites: Writes[ModelEntity] = Json.writes[ModelEntity]

}
