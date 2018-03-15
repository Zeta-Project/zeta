package de.htwg.zeta.common.format.project.gdsl

import de.htwg.zeta.common.format.project.gdsl.shape.ShapeFormat
import de.htwg.zeta.common.models.project.gdsl.shape.Shape
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class ShapesFormat(
    shapeFormat: ShapeFormat,
    sShapes: String = "shapes"
) extends OFormat[List[Shape]] {

  override def writes(clazz: List[Shape]): JsObject = Json.obj(
    sShapes -> Writes.list(shapeFormat).writes(clazz)
  )

  override def reads(json: JsValue): JsResult[List[Shape]] =
    (json \ sShapes).validate(Reads.list(shapeFormat))

}
