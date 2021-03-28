package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import java.util.UUID

import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Compartement
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Ellipse
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Triangle
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Hexagon
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Octagon
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Diamond
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Star8
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.GeoModel
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.HorizontalLayout
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Line
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Polygon
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Polyline
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Rectangle
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.RepeatingBox
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.RoundedRectangle
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.StaticText
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.TextField
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.VerticalLayout
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat

class GeoModelFormat(
    lineFormat: LineFormat,
    polylineFormat: PolylineFormat,
    polygonFormat: PolygonFormat,
    rectangleFormat: RectangleFormat,
    roundedRectangleFormat: RoundedRectangleFormat,
    ellipseFormat: EllipseFormat,
    triangleFormat: TriangleFormat,
    hexagonFormat: HexagonFormat,
    octagonFormat: OctagonFormat,
    diamondFormat: DiamondFormat,
    star8Format: Star8Format,
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
      case p: Line => lineFormat.writes(p)
      case p: Polyline => polylineFormat.writes(p)
      case p: Polygon => polygonFormat.writes(p)
      case p: Rectangle => rectangleFormat.writes(p)
      case p: RoundedRectangle => roundedRectangleFormat.writes(p)
      case p: Ellipse => ellipseFormat.writes(p)
      case p: Triangle => triangleFormat.writes(p)
      case p: Hexagon => hexagonFormat.writes(p)
      case p: Octagon => octagonFormat.writes(p)
      case p: Diamond => diamondFormat.writes(p)
      case p: Star8 => star8Format.writes(p)
      case p: StaticText => staticTextFormat.writes(p)
      case p: TextField => textFieldFormat.writes(p)
      case p: Compartement => compartementFormat.writes(p)
      case p: RepeatingBox => repeatingBoxFormat.writes(p)
      case p: HorizontalLayout => horizontalLayoutFormat.writes(p)
      case p: VerticalLayout => verticalLayoutFormat.writes(p)
    }
   addId(json, clazz)
  }

  private def addId(json: JsObject, geoModel: GeoModel): JsObject = {
    val id = calculateId(geoModel)
    json + ("id" -> Json.toJson(id))
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
      case triangleFormat.vType => triangleFormat.reads(json)
      case hexagonFormat.vType => hexagonFormat.reads(json)
      case octagonFormat.vType => octagonFormat.reads(json)
      case diamondFormat.vType => diamondFormat.reads(json)
      case star8Format.vType => star8Format.reads(json)
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
    TriangleFormat(geoModelFormatProvider),
    HexagonFormat(geoModelFormatProvider),
    OctagonFormat(geoModelFormatProvider),
    DiamondFormat(geoModelFormatProvider),
    Star8Format(geoModelFormatProvider),
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
