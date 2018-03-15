package de.htwg.zeta.common.format.project.gdsl.shape

import de.htwg.zeta.common.models.project.gdsl.shape.Size
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class SizeFormat(
    sWidth: String = "width",
    sHeight: String = "height",
    sWidthMax: String = "widthMax",
    sWidthMin: String = "widthMin",
    sHeightMax: String = "heightMax",
    sHeightMin: String = "heightMin"
) extends OFormat[Size] {

  override def writes(clazz: Size): JsObject = Json.obj(
    sWidth -> clazz.width,
    sHeight -> clazz.height,
    sWidthMax -> clazz.widthMax,
    sWidthMin -> clazz.widthMin,
    sHeightMax -> clazz.heightMax,
    sHeightMin -> clazz.heightMin
  )

  override def reads(json: JsValue): JsResult[Size] = for {
    width <- (json \ sWidth).validate[Int]
    height <- (json \ sHeight).validate[Int]
    widthMax <- (json \ sWidthMax).validate[Int]
    widthMin <- (json \ sWidthMin).validate[Int]
    heightMax <- (json \ sHeightMax).validate[Int]
    heightMin <- (json \ sHeightMin).validate[Int]
  } yield {
    Size(
      width = width,
      height = height,
      widthMax = widthMax,
      widthMin = widthMin,
      heightMax = heightMax,
      heightMin = heightMin
    )
  }

}
