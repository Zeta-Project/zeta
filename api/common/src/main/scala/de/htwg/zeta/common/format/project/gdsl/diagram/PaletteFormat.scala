package de.htwg.zeta.common.format.project.gdsl.diagram

import de.htwg.zeta.common.models.project.gdsl.diagram.Palette
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat

class PaletteFormat(
    sName: String,
    sNodes: String
) extends OFormat[Palette] {

  override def writes(clazz: Palette): JsObject = Json.obj(
    sName -> clazz.name,
    sNodes -> clazz.nodes.map(_.name)
  )

  override def reads(json: JsValue): JsResult[Palette] = for {
    name <- (json \ sName).validate[String]
    nodes <- JsSuccess(List()) // TODO
  } yield {
    Palette(name, nodes)
  }

}
object PaletteFormat {
  def apply(): PaletteFormat = new PaletteFormat("name", "nodes")
}

