package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Ellipse
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.GeoModel
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat

class GeoModelFormat(
    lineFormat: LineFormat,
    polylineFormat: PolylineFormat,
    polygonFormat: PolygonFormat,
    rectangleFormat: RectangleFormat,
    roundedRectangleFormat: RoundedRectangleFormat,
    ellipseFormat: EllipseFormat,
    staticTextFormat: StaticTextFormat,
    textFieldFormat: TextFieldFormat,
    compartementFormat: CompartementFormat,
    repeatingBoxFormat: RepeatingBoxFormat,
    horizontalLayoutFormat: HorizontalLayoutFormat,
    verticalLayoutFormat: VerticalLayoutFormat,
    sType: String
) extends OFormat[GeoModel] {

  override def writes(clazz: GeoModel): JsObject = clazz match {
    case p: Ellipse => ellipseFormat.writes(p)
  }

  override def reads(json: JsValue): JsResult[GeoModel] =
    (json \ sType).validate[String].getOrElse("") match {
      case lineFormat.vType => lineFormat.reads(json)
      case polylineFormat.vType => polylineFormat.reads(json)
      case polygonFormat.vType => polygonFormat.reads(json)
      case rectangleFormat.vType => rectangleFormat.reads(json)
      case roundedRectangleFormat.vType => roundedRectangleFormat.reads(json)
      case ellipseFormat.vType => ellipseFormat.reads(json)
      case staticTextFormat.vType => staticTextFormat.reads(json)
      case textFieldFormat.vType => textFieldFormat.reads(json)
      case compartementFormat.vType => compartementFormat.reads(json)
      case repeatingBoxFormat.vType => repeatingBoxFormat.reads(json)
      case horizontalLayoutFormat.vType => horizontalLayoutFormat.reads(json)
      case verticalLayoutFormat.vType => verticalLayoutFormat.reads(json)
    }

}
object GeoModelFormat {
  val geoModelFormat: GeoModelFormat = new GeoModelFormat(
    LineFormat(geoModelFormatProvider),
    PolylineFormat(geoModelFormatProvider),
    PolygonFormat(geoModelFormatProvider),
    RectangleFormat(geoModelFormatProvider),
    RoundedRectangleFormat(geoModelFormatProvider),
    EllipseFormat(geoModelFormatProvider),
    StaticTextFormat(geoModelFormatProvider),
    TextFieldFormat(geoModelFormatProvider),
    CompartementFormat(geoModelFormatProvider),
    RepeatingBoxFormat(geoModelFormatProvider),
    HorizontalLayoutFormat(geoModelFormatProvider),
    VerticalLayoutFormat(geoModelFormatProvider),
    "type"
  )

  def geoModelFormatProvider(): GeoModelFormat = geoModelFormat

  def apply(): GeoModelFormat = geoModelFormat
}
