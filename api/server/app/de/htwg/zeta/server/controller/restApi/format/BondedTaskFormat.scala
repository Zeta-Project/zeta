package de.htwg.zeta.server.controller.restApi.format

import de.htwg.zeta.common.models.entity.BondedTask
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.Writes

/**
 * @author Philipp Daniels
 */
object BondedTaskFormat extends Writes[BondedTask] {

  override def writes(o: BondedTask): JsValue = {
    Json.obj(
      "id" -> o.id.toString,
      "name" -> o.name,
      "generatorId" -> o.generatorId,
      "filterId" -> o.filterId,
      "menu" -> o.menu,
      "item" -> o.item
    )
  }
}
