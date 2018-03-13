package de.htwg.zeta.common.format.project

import java.util.UUID

import de.htwg.zeta.common.models.entity.GraphicalDslRelease
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class GraphicalDslReleaseFormat(
    conceptFormat: ConceptFormat,
    sId: String = "id",
    sName: String = "name",
    sConcept: String = "concept",
    sDiagram: String = "diagram",
    sShape: String = "shape",
    sStyle: String = "style",
    sVersion: String = "version"
) extends OFormat[GraphicalDslRelease] {

  override def writes(release: GraphicalDslRelease): JsObject = Json.obj(
    sId -> release.id,
    sName -> release.name,
    sConcept -> conceptFormat.writes(release.concept),
    sDiagram -> release.diagram,
    sShape -> release.shape,
    sStyle -> release.style,
    sVersion -> release.version
  )

  override def reads(json: JsValue): JsResult[GraphicalDslRelease] = {
    for {
      id <- (json \ sId).validate[UUID]
      name <- (json \ sName).validate[String]
      concept <- (json \ sConcept).validate(conceptFormat)
      diagram <- (json \ sDiagram).validate[String]
      shape <- (json \ sShape).validate[String]
      style <- (json \ sStyle).validate[String]
      version <- (json \ sVersion).validate[String]
    } yield {
      GraphicalDslRelease(id, name, concept, diagram, shape, style, version)
    }
  }

}
