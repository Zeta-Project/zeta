package de.htwg.zeta.common.format.model

import de.htwg.zeta.common.format.project.GdslProjectFormat
import de.htwg.zeta.common.models.project.instance.GdslInstanceProject
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class GDSLInstanceProjectFormat(
    gDSLInstanceFormat: GraphicalDslInstanceFormat,
    gDSLProjectFormat: GdslProjectFormat,
    gDSLInstance: String = "gDSLInstance",
    gDSLProject: String = "gDSLProject"
) extends OFormat[GdslInstanceProject] {

  override def writes(instance: GdslInstanceProject): JsObject = Json.obj(
    gDSLInstance -> gDSLInstanceFormat.writes(instance.gDSLInstance),
    gDSLProject -> gDSLProjectFormat.writes(instance.gDSLProject)
  )

  override def reads(json: JsValue): JsResult[GdslInstanceProject] = for {
    gDSLI <- (json \ gDSLInstance).validate(gDSLInstanceFormat.reads)
    gDSLR <- (json \ gDSLProject).validate(gDSLProjectFormat.reads)
  } yield {
    GdslInstanceProject(
      gDSLInstance = gDSLI,
      gDSLProject = gDSLR
    )
  }
}
