package de.htwg.zeta.common.format.project.gdsl.shape

import de.htwg.zeta.common.models.project.gdsl.shape.PlacingPosition
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat

class PlacingPositionFormat(
    sDistance: String,
    sOffset: String
) extends OFormat[PlacingPosition] {

  override def writes(clazz: PlacingPosition): JsObject = Json.obj(
    sDistance -> clazz.distance,
    sOffset -> clazz.offset
  )

  override def reads(json: JsValue): JsResult[PlacingPosition] = for {
    distance <- (json \ sDistance).validate[Int]
    offset <- (json \ sOffset).validate[Double]
  } yield {
    PlacingPosition(distance, offset)
  }

}
object PlacingPositionFormat {
  def apply(): PlacingPositionFormat = new PlacingPositionFormat(
    "distance",
    "offset"
  )
}
