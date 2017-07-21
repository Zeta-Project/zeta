package de.htwg.zeta.server.controller.restApi.format

import java.util.UUID

import de.htwg.zeta.common.models.entity.BondedTask
import play.api.libs.json.Format
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue

/**
 * Parse JsValue to BondedTask and BondedTask to JsValue
 */
object BondedTaskFormat extends Format[BondedTask] {

  val attributeId = "id"
  val attributeName = "name"
  val attributeGenerator = "generatorId"
  val attributeFilter = "filterId"
  val attributeMenu = "menu"
  val attributeItem = "item"

  override def writes(o: BondedTask): JsValue = {
    Json.obj(
      attributeId -> o.id.toString,
      attributeName -> o.name,
      attributeGenerator -> o.generatorId,
      attributeFilter -> o.filterId,
      attributeMenu -> o.menu,
      attributeItem -> o.item
    )
  }

  override def reads(json: JsValue): JsResult[BondedTask] = {
    for {
      name <- (json \ attributeName).validate[String]
      generator <- (json \ attributeGenerator).validate[UUID]
      filter <- (json \ attributeFilter).validate[UUID]
      menu <- (json \ attributeMenu).validate[String]
      item <- (json \ attributeItem).validate[String]
    } yield {
      BondedTask(UUID.randomUUID(), name, generator, filter, menu, item)
    }
  }
}
