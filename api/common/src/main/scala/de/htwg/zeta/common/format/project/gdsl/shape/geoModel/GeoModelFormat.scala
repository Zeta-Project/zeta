package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import java.util.UUID

import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Ellipse
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.GeoModel
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.VerticalLayout
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.HorizontalLayout
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.RepeatingBox
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Compartement
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.TextField
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.StaticText
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.RoundedRectangle
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Rectangle
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Polygon
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Polyline
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Line
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat
import play.api.libs.json.Json

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

  override def writes(clazz: GeoModel): JsObject = {
    val json = clazz match {
      case p:Line => Json.obj(lineFormat.vType -> lineFormat.writes(p))
      case p:Polyline => Json.obj(polylineFormat.vType -> polylineFormat.writes(p))
      case p:Polygon => Json.obj(polygonFormat.vType -> polygonFormat.writes(p))
      case p:Rectangle => Json.obj(rectangleFormat.vType -> rectangleFormat.writes(p))
      case p:RoundedRectangle => Json.obj(roundedRectangleFormat.vType -> roundedRectangleFormat.writes(p))
      case p:Ellipse => Json.obj(ellipseFormat.vType -> ellipseFormat.writes(p))
      case p:StaticText => Json.obj(staticTextFormat.vType -> staticTextFormat.writes(p))
      case p:TextField => Json.obj(textFieldFormat.vType -> textFieldFormat.writes(p))
      case p:Compartement => Json.obj(compartementFormat.vType -> compartementFormat.writes(p))
      case p:RepeatingBox => Json.obj(repeatingBoxFormat.vType -> repeatingBoxFormat.writes(p))
      case p:HorizontalLayout => Json.obj(horizontalLayoutFormat.vType -> horizontalLayoutFormat.writes(p))
      case p:VerticalLayout => Json.obj(verticalLayoutFormat.vType -> verticalLayoutFormat.writes(p))
    }

    json + ("id" -> JsString(calculateId(clazz)))
  }

  //noinspection ScalaStyle
  private def calculateId(geoModel: GeoModel): String = {
    val hexString = geoModel.hashCode().toHexString
    val uuidWithoutDashes = hexString.reverse.padTo(32, "0").reverse.mkString
    val uuidWithDashes = new StringBuilder(uuidWithoutDashes)
    uuidWithDashes.insert(8, "-")
    uuidWithDashes.insert(13, "-")
    uuidWithDashes.insert(18, "-")
    uuidWithDashes.insert(23, "-")
    val uuid = UUID.fromString(uuidWithDashes.toString())
    uuid.toString
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
