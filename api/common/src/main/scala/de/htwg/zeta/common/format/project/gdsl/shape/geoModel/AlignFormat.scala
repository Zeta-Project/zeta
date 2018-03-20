package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Align
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Align.Horizontal
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Align.Horizontal.HorizontalAlignment
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Align.Vertical
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Align.Vertical.VerticalAlignment
import play.api.libs.json.JsError
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads


class HorizontalAlignmentFormat extends Reads[HorizontalAlignment] {
  override def reads(json: JsValue): JsResult[HorizontalAlignment] = json.validate[String].getOrElse("") match {
    case "left" => JsSuccess(Horizontal.left)
    case "middle" => JsSuccess(Horizontal.middle)
    case "right" => JsSuccess(Horizontal.right)
    case _ => JsError()
  }
}
class VerticalAlignmentFormat extends Reads[VerticalAlignment] {
  override def reads(json: JsValue): JsResult[VerticalAlignment] = json.validate[String].getOrElse("") match {
    case "top" => JsSuccess(Vertical.top)
    case "middle" => JsSuccess(Vertical.middle)
    case "bottom" => JsSuccess(Vertical.bottom)
    case _ => JsError()
  }
}

class AlignFormat(
    verticalAlignmentFormat: VerticalAlignmentFormat,
    horizontalAlignmentFormat: HorizontalAlignmentFormat,
    sHorizontal: String,
    sVertical: String
) extends OFormat[Align] {

  override def writes(clazz: Align): JsObject = Json.obj(
    sHorizontal -> clazz.horizontal,
    sVertical -> clazz.vertical
  )

  override def reads(json: JsValue): JsResult[Align] = for {
    horizontal <- (json \ sHorizontal).validate(horizontalAlignmentFormat)
    vertical <- (json \ sVertical).validate(verticalAlignmentFormat)
  } yield {
    Align(horizontal, vertical)
  }

}
object AlignFormat {
  def apply(): AlignFormat = new AlignFormat(
    new VerticalAlignmentFormat(),
    new HorizontalAlignmentFormat(),
    "horizontal",
    "vertical")
}

