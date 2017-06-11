package models.entity

import java.util.UUID

import models.modelDefinitions.helper.HLink
import models.modelDefinitions.metaModel.Dsl
import models.modelDefinitions.metaModel.MetaModel
import play.api.libs.json.Format
import play.api.libs.json.Json


case class MetaModelEntity(
    id: UUID = UUID.randomUUID,
    rev: String,
    name: String,
    metaModel: MetaModel,
    dsl: Dsl = Dsl(),
    links: Option[Seq[HLink]] = None
) extends Entity

object MetaModelEntity {

  implicit val playJsonFormat: Format[MetaModelEntity] = Json.format[MetaModelEntity]

}
