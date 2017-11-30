package de.htwg.zeta.common.format.entity

import java.util.UUID

import de.htwg.zeta.common.models.entity.BondedTask
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat

/**
 * Parse JsValue to BondedTask and BondedTask to JsValue
 */
@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class BondedTaskFormat(
    sId: String = "id",
    sName: String = "name",
    sGeneratorId: String = "generatorId",
    sFilterId: String = "filterId",
    sMenu: String = "menu",
    sItem: String = "item",
    sDeleted: String = "deleted"
) extends OFormat[BondedTask] {

  override def writes(task: BondedTask): JsObject = Json.obj(
    sId -> task.id.toString,
    sName -> task.name,
    sGeneratorId -> task.generatorId,
    sFilterId -> task.filterId,
    sMenu -> task.menu,
    sItem -> task.item,
    sDeleted -> task.deleted
  )

  override def reads(json: JsValue): JsResult[BondedTask] = for {
    id <- (json \ sId).validateOpt[UUID]
    name <- (json \ sName).validate[String]
    generator <- (json \ sGeneratorId).validate[UUID]
    filter <- (json \ sFilterId).validate[UUID]
    menu <- (json \ sMenu).validate[String]
    item <- (json \ sItem).validate[String]
    deleted <- (json \ sDeleted).validateOpt[Boolean]
  } yield {
    BondedTask(id.getOrElse(UUID.randomUUID()), name, generator, filter, menu, item, deleted.getOrElse(false))
  }

}
