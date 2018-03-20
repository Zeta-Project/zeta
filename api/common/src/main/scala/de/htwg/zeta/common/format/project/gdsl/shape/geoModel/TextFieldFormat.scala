package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.format.project.gdsl.style.StyleFormat
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.TextField
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

class TextFieldFormat(
    geoModelFormatProvider: () => GeoModelFormat,
    sizeFormat: SizeFormat,
    positionFormat: PositionFormat,
    alignFormat: AlignFormat,
    styleFormat: StyleFormat,
    sType: String,
    sIdentifier: String,
    sSize: String,
    sPosition: String,
    sEditable: String,
    sMultiline: String,
    sAlign: String,
    sChildGeoModels: String,
    sStyle: String
) extends OFormat[TextField] {

  val vType: String = "textfield"

  override def writes(clazz: TextField): JsObject = Json.obj(
    sType -> vType,
    sIdentifier -> clazz.identifier,
    sSize -> sizeFormat.writes(clazz.size),
    sPosition -> positionFormat.writes(clazz.position),
    sEditable -> clazz.editable,
    sMultiline -> clazz.multiline,
    sAlign -> alignFormat.writes(clazz.align),
    sChildGeoModels -> Writes.list(geoModelFormatProvider()).writes(clazz.childGeoModels),
    sStyle -> styleFormat.writes(clazz.style)
  )

  override def reads(json: JsValue): JsResult[TextField] = for {
    identifier <- (json \ sIdentifier).validate[String]
    size <- (json \ sSize).validate(sizeFormat)
    position <- (json \ sPosition).validate(positionFormat)
    editable <- (json \ sEditable).validate[Boolean]
    multiline <- (json \ sMultiline).validate[Boolean]
    align <- (json \ sAlign).validate(alignFormat)
    childGeoModels <- (json \ sChildGeoModels).validate(Reads.list(geoModelFormatProvider()))
    style <- (json \ sStyle).validate(styleFormat)
  } yield {
    TextField(
      identifier,
      size,
      position,
      editable,
      multiline,
      align,
      childGeoModels,
      style
    )
  }

}
object TextFieldFormat {
  def apply(geoModelFormat: () => GeoModelFormat): TextFieldFormat = new TextFieldFormat(
    geoModelFormat,
    SizeFormat(),
    PositionFormat(),
    AlignFormat(),
    StyleFormat(),
    "type",
    "identifier",
    "size",
    "position",
    "editable",
    "multiline",
    "align",
    "childGeoElements",
    "style"
  )
}

