package de.htwg.zeta.common.format.project.gdsl.shape

import de.htwg.zeta.common.models.project.gdsl.shape.PlacingPosition
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat

class PlacingPositionFormat(
    sOffset: String
) extends OFormat[PlacingPosition] {

  override def writes(clazz: PlacingPosition): JsObject = Json.obj(
    sOffset -> clazz.offset
  )

  override def reads(json: JsValue): JsResult[PlacingPosition] = for {
    offset <- (json \ sOffset).validate[Double]
  } yield {
    PlacingPosition(offset)
  }

}
object PlacingPositionFormat {
  def apply(): PlacingPositionFormat = new PlacingPositionFormat(
    "offset"
  )
}
