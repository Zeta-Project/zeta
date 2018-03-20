package de.htwg.zeta.common.format.project.gdsl.diagram

import de.htwg.zeta.common.models.project.gdsl.diagram.Diagram
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

class DiagramFormat(
    paletteFormat: PaletteFormat,
    sName: String,
    sPalettes: String
) extends OFormat[Diagram] {

  override def writes(clazz: Diagram): JsObject = Json.obj(
    sName -> clazz.name,
    sPalettes -> Writes.list(paletteFormat).writes(clazz.palettes)
  )

  override def reads(json: JsValue): JsResult[Diagram] = for {
    name <- (json \ sName).validate[String]
    palettes <- (json \ sPalettes).validate(Reads.list(paletteFormat))
  } yield {
    Diagram(name, palettes)
  }

}
object DiagramFormat {
  def apply(): DiagramFormat = new DiagramFormat(
    PaletteFormat(),
    "name",
    "palettes"
  )
}
