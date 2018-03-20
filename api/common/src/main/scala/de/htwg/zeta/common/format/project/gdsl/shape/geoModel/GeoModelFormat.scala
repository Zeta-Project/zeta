package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.format.project.gdsl.style.StyleFormat
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Ellipse
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.GeoModel
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat

class GeoModelFormat(
    ellipseFormat: EllipseFormat,
    sType: String
) extends OFormat[GeoModel] {

  override def writes(clazz: GeoModel): JsObject = clazz match {
    case p: Ellipse => ellipseFormat.writes(p)
  }

  override def reads(json: JsValue): JsResult[GeoModel] =
    (json \ sType).validate[String].getOrElse("") match {
      case ellipseFormat.vType => ellipseFormat.reads(json)
    }

}
object GeoModelFormat {
  val geoModelFormat: GeoModelFormat = new GeoModelFormat(
    EllipseFormat(StyleFormat(), geoModelFormatProvider),
    "type"
  )

  def geoModelFormatProvider(): GeoModelFormat = geoModelFormat

  def apply(): GeoModelFormat = geoModelFormat
}
