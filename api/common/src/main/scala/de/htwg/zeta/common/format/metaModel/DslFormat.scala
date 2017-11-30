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
import play.api.libs.json.Writes

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class DslFormat(
    sDiagram: String = "diagram",
    sShape: String = "shape",
    sStyle: String = "style",
    sCode: String = "code"
) extends OFormat[Dsl] {

  override def writes(dsl: Dsl): JsObject = Json.obj(
    sDiagram -> Writes.optionWithNull(diagram).writes(dsl.diagram),
    sShape -> Writes.optionWithNull(shape).writes(dsl.shape),
    sStyle -> Writes.optionWithNull(style).writes(dsl.style)
  )

  override def reads(json: JsValue): JsResult[Dsl] = for {
    diagram <- (json \ sDiagram).validateOpt(diagram)
    shape <- (json \ sShape).validateOpt(shape)
    style <- (json \ sStyle).validateOpt(style)
  } yield {
    Dsl(diagram, shape, style)
  }

  val diagram: OFormat[Diagram] = new OFormat[Diagram] {

    override def writes(diagram: Diagram): JsObject = Json.obj(
      sCode -> diagram.code
    )

    override def reads(json: JsValue): JsResult[Diagram] = {
      (json \ sCode).validate[String].map(Diagram)
    }

  }

  val shape: OFormat[Shape] = new OFormat[Shape] {

    override def writes(shape: Shape): JsObject = Json.obj(
      sCode -> shape.code
    )

    override def reads(json: JsValue): JsResult[Shape] = {
      (json \ sCode).validate[String].map(Shape)
    }

  }

  val style: OFormat[Style] = new OFormat[Style] {

    override def writes(style: Style): JsObject = Json.obj(
      sCode -> style.code
    )

    override def reads(json: JsValue): JsResult[Style] = {
      (json \ sCode).validate[String].map(Style)
    }

  }

}
