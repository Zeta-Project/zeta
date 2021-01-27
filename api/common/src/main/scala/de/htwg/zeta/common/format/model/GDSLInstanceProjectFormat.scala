package de.htwg.zeta.common.format.model

import de.htwg.zeta.common.format.project.gdsl.DiagramsFormat
import de.htwg.zeta.common.format.project.gdsl.StylesFormat
import de.htwg.zeta.common.format.project.gdsl.shape.ShapeFormat
import de.htwg.zeta.common.format.project.ConceptFormat
import de.htwg.zeta.common.models.project.instance.GdslInstanceProject
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class GDSLInstanceProjectFormat(
    gDSLInstanceFormat: GraphicalDslInstanceFormat,
    conceptFormat: ConceptFormat,
    shapeFormat: ShapeFormat,
    diagramsFormat: DiagramsFormat,
    stylesFormat: StylesFormat,
    gDSLInstance: String = "model",
    concept: String = "concept",
    shape: String = "shape",
    diagram: String = "diagram",
    style: String = "style"
) extends OFormat[GdslInstanceProject] {

  override def writes(instance: GdslInstanceProject): JsObject = Json.obj(
    gDSLInstance -> gDSLInstanceFormat.writes(instance.gDSLInstance),
    concept -> conceptFormat.writes(instance.concept),
    shape -> shapeFormat.writes(instance.shape),
    diagram -> diagramsFormat.writes(instance.diagram),
    style -> stylesFormat.writes(instance.style)
  )

  override def reads(json: JsValue): JsResult[GdslInstanceProject] = for {
    gDSLI <- (json \ gDSLInstance).validate(gDSLInstanceFormat.reads)
    co <- (json \ concept).validate(conceptFormat.reads)
    sh <- (json \ shape).validate(shapeFormat.reads)
    di <- (json \ diagram).validate(diagramsFormat.reads)
    st <- (json \ style).validate(stylesFormat.reads)
  } yield {
    GdslInstanceProject(
      gDSLInstance = gDSLI,
      concept = co,
      shape = sh,
      diagram = di,
      style = st
    )
  }
}
