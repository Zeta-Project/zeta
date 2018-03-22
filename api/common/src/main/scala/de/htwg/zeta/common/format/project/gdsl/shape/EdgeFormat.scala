package de.htwg.zeta.common.format.project.gdsl.shape

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
    sName: String,
    sConceptElement: String,
    sTarget: String,
    sPlacings: String
) extends OFormat[Edge] {

  override def writes(clazz: Edge): JsObject = Json.obj(
    sName -> clazz.name,
    sConceptElement -> clazz.conceptElement,
    sTarget -> clazz.target,
    sPlacings -> Writes.list(placingFormat).writes(clazz.placings)
  )

  override def reads(json: JsValue): JsResult[Edge] = for {
    name <- (json \ sName).validate[String]
    conceptElement <- (json \ sConceptElement).validate[String]
    target <- (json \ sTarget).validate[String]
    placings <- (json \ sPlacings).validate(Reads.list(placingFormat))
  } yield {
    Edge(name, conceptElement, target, placings)
  }

}
object EdgeFormat {
  def apply(): EdgeFormat = new EdgeFormat(
    PlacingFormat(),
    "name",
    "conceptElement",
    "target",
    "placings"
  )
}
