package de.htwg.zeta.server.controller.restApi.metaModelUiFormat

import de.htwg.zeta.common.models.entity.MetaModelRelease
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Dsl
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.Writes

/**
 * @author Philipp Daniels
 */
object MetaModelReleaseFormat extends Writes[MetaModelRelease] {

  override def writes(o: MetaModelRelease): JsValue = {
    Json.obj(
      "id" -> o.id.toString,
      "name" -> o.name,
      "metaModel" -> MetaModelFormat.writes(o.metaModel),
      "dsl" ->  Dsl.dslFormat.writes(o.dsl),
      "version" -> o.version
    )
  }
}
