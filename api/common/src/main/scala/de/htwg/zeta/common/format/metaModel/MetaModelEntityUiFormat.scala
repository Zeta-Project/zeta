package de.htwg.zeta.common.format.metaModel

import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import play.api.libs.json.Format
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json

/**
 */
object MetaModelEntityUiFormat extends Format[MetaModelEntity] {
  private val format = {
    implicit val metaModelFormat: Format[MetaModel] = MetaModelFormat
    Json.format[MetaModelEntity]
  }

  override def writes(o: MetaModelEntity): JsValue = format.writes(o)

  override def reads(json: JsValue): JsResult[MetaModelEntity] = format.reads(json)
}
