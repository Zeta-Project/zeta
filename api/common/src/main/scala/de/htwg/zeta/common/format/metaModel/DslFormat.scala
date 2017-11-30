package de.htwg.zeta.common.format.metaModel

import de.htwg.zeta.common.models.modelDefinitions.metaModel.Diagram
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Dsl
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Shape
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Style
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class DslFormat(
    sDiagram: String = "diagram",
    sShape: String = "shape",
    sStyle: String = "style",
    sCode: String = "code"
) extends OFormat[Dsl] {

  override def writes(dsl: Dsl): JsObject = Json.obj(
    sDiagram -> Writes.optionWithNull[String].writes(dsl.diagram.map(_.code)),
    sShape -> Writes.optionWithNull[String].writes(dsl.shape.map(_.code)),
    sStyle -> Writes.optionWithNull[String].writes(dsl.style.map(_.code))
  )

  override def reads(json: JsValue): JsResult[Dsl] = for {
    diagram <- (json \ sDiagram).validate(Reads.optionNoError[String])
    shape <- (json \ sShape).validate(Reads.optionNoError[String])
    style <- (json \ sStyle).validate(Reads.optionNoError[String])
  } yield {
    Dsl(diagram.map(Diagram.apply), shape.map(Shape.apply), style.map(Style.apply))
  }

}
