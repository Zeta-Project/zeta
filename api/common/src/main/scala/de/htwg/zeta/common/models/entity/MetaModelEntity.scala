package de.htwg.zeta.common.models.entity

import java.util.UUID

import de.htwg.zeta.common.models.modelDefinitions.helper.HLink
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Dsl
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import play.api.libs.json.Format
import play.api.libs.json.Json


case class MetaModelEntity(
    id: UUID,
    rev: String,
    name: String,
    metaModel: MetaModel,
    dsl: Dsl = Dsl(),
    validator: Option[String] = None,
    links: Option[Seq[HLink]] = None
) extends Entity

object MetaModelEntity {

  implicit val playJsonFormat: Format[MetaModelEntity] = Json.format[MetaModelEntity]

}
