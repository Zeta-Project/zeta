package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Point
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat

class PointFormat(
    sX: String,
    sY: String
) extends OFormat[Point] {

  override def writes(clazz: Point): JsObject = Json.obj(
    sX -> clazz.x,
    sY -> clazz.y
  )

  override def reads(json: JsValue): JsResult[Point] = for {
    x <- (json \ sX).validate[Int]
    y <- (json \ sY).validate[Int]
  } yield {
    Point(x = x, y = y)
  }

}
object PointFormat {
  def apply(): PointFormat = new PointFormat("x", "y")
}

