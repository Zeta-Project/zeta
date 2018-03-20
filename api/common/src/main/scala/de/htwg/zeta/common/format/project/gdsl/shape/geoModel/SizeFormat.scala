package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Size
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat

class SizeFormat(
    sWidth: String,
    sHeight: String
) extends OFormat[Size] {

  override def writes(clazz: Size): JsObject = Json.obj(
    sWidth -> clazz.width,
    sHeight -> clazz.height
  )

  override def reads(json: JsValue): JsResult[Size] = for {
    width <- (json \ sWidth).validate[Int]
    height <- (json \ sHeight).validate[Int]
  } yield {
    Size(width = width, height = height)
  }

}
object SizeFormat {
  def apply(): SizeFormat = new SizeFormat("width", "height")
}
