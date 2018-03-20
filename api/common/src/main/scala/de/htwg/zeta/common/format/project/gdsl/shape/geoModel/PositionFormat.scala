package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Position
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat

class PositionFormat(
    sX: String = "x",
    sY: String = "y"
) extends OFormat[Position] {

  override def writes(clazz: Position): JsObject = Json.obj(
    sX -> clazz.x,
    sY -> clazz.y
  )

  override def reads(json: JsValue): JsResult[Position] = for {
    x <- (json \ sX).validate[Int]
    y <- (json \ sY).validate[Int]
  } yield {
    Position(x = x, y = y)
  }

}
object PositionFormat {
  def apply(): PositionFormat = new PositionFormat("x", "y")
}
