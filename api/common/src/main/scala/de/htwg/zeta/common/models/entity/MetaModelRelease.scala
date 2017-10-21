package de.htwg.zeta.common.models.entity

import java.util.UUID

import de.htwg.zeta.common.models.modelDefinitions.metaModel.Dsl
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import play.api.libs.json.Format
import play.api.libs.json.Json

case class MetaModelRelease(
    id: UUID,
    name: String,
    metaModel: MetaModel,
    dsl: Dsl,
    version: String
) extends Entity

object MetaModelRelease{

  implicit val playJsonFormats: Format[MetaModelRelease] = Json.format[MetaModelRelease]

}
