package de.htwg.zeta.common.format.project.gdsl.shape

import de.htwg.zeta.common.models.project.gdsl.shape.Position
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat

class PositionFormat(
    sDistance: String,
    sOffset: String
) extends OFormat[Position] {

  override def writes(clazz: Position): JsObject = Json.obj(
    sDistance -> clazz.distance,
    sOffset -> clazz.offset
  )

  override def reads(json: JsValue): JsResult[Position] = for {
    distance <- (json \ sDistance).validate[Int]
    offset <- (json \ sOffset).validate[Double]
  } yield {
    Position(distance, offset)
  }

}
object PositionFormat {
  def apply(): PositionFormat = new PositionFormat(
    "distance",
    "offset"
  )
}
