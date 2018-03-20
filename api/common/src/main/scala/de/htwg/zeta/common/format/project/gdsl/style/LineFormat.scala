package de.htwg.zeta.common.format.project.gdsl.style

import de.htwg.zeta.common.models.project.gdsl.style.Dashed
import de.htwg.zeta.common.models.project.gdsl.style.Dotted
import de.htwg.zeta.common.models.project.gdsl.style.DoubleLine
import de.htwg.zeta.common.models.project.gdsl.style.Line
import de.htwg.zeta.common.models.project.gdsl.style.LineStyle
import de.htwg.zeta.common.models.project.gdsl.style.Solid
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads

class LineFormat(
    colorFormat: ColorFormat,
    sColor: String,
    sStyle: String,
    sWidth: String
) extends OFormat[Line] {

  override def writes(clazz: Line): JsObject = Json.obj(
    sColor -> colorFormat.writes(clazz.color),
    sStyle -> writeLineStyle(clazz.style),
    sWidth -> clazz.width
  )

  override def reads(json: JsValue): JsResult[Line] = for {
    color <- (json \ sColor).validate(colorFormat)
    style <- (json \ sStyle).validate(LineStyleRead())
    width <- (json \ sWidth).validate[Int]
  } yield {
    Line(color, style, width)
  }

  private def writeLineStyle(lineStyle: LineStyle): String = lineStyle match {
    case _: Dotted => "dotted"
    case _: Solid => "solid"
    case _: DoubleLine => "double"
    case _: Dashed => "dashed"
  }

  protected class LineStyleRead extends Reads[LineStyle] {
    override def reads(lineStyle: JsValue): JsResult[LineStyle] = lineStyle.toString match {
      case "dotted" => JsSuccess(Dotted())
      case "solid" => JsSuccess(Solid())
      case "double" => JsSuccess(DoubleLine())
      case "dashed" => JsSuccess(Dashed())
    }
  }
  protected object LineStyleRead {
    def apply(): LineStyleRead = new LineStyleRead()
  }

}
object LineFormat {
  def apply(): LineFormat = new LineFormat(
    ColorFormat(),
    "color", "style", "width"
  )
}
