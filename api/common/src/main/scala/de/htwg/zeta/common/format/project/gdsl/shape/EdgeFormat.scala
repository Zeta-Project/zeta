package de.htwg.zeta.common.format.project.gdsl.shape

import de.htwg.zeta.common.format.project.gdsl.style.StyleFormat
import de.htwg.zeta.common.models.project.gdsl.shape.Edge
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

class EdgeFormat(
    placingFormat: PlacingFormat,
    styleFormat: StyleFormat,
    metaFormat: MetaFormat,
    sName: String,
    sConceptElement: String,
    sTarget: String,
    sStyle: String,
    sPlacings: String,
    sMeta: String
) extends OFormat[Edge] {

  override def writes(clazz: Edge): JsObject = Json.obj(
    sName -> clazz.name,
    sConceptElement -> clazz.conceptElement,
    sTarget -> clazz.target,
    sStyle -> styleFormat.writes(clazz.style),
    sPlacings -> Writes.list(placingFormat).writes(clazz.placings),
    sMeta -> metaFormat.writes(clazz)
  )

  override def reads(json: JsValue): JsResult[Edge] = for {
    name <- (json \ sName).validate[String]
    conceptElement <- (json \ sConceptElement).validate[String]
    target <- (json \ sTarget).validate[String]
    style <- (json \ sStyle).validate(styleFormat)
    placings <- (json \ sPlacings).validate(Reads.list(placingFormat))
  } yield {
    Edge(name, conceptElement, target, style, placings)
  }

}
object EdgeFormat {
  def apply(): EdgeFormat = new EdgeFormat(
    PlacingFormat(),
    StyleFormat(),
    MetaFormat(),
    "name",
    "conceptElement",
    "target",
    "style",
    "placings",
    "meta"
  )
}
