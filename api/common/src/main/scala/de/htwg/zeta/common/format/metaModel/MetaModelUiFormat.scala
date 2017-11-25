package de.htwg.zeta.common.format.metaModel

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import play.api.libs.json.Format
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue

/**
 */
object MetaModelUiFormat extends Format[MetaModel] {


  override def writes(o: MetaModel): JsValue = MetaModelFormat.writes(o)

  override def reads(json: JsValue): JsResult[MetaModel] = MetaModelFormat.reads(json)
}
