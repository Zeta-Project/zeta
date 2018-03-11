package de.htwg.zeta.common.format.metaModel

import java.util.UUID

import de.htwg.zeta.common.models.project.GraphicalDsl
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat
import play.api.libs.json.Reads

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class GraphicalDslFormat(
    conceptFormat: ConceptFormat,
    sId: String = "id",
    sName: String = "name",
    sConcept: String = "concept",
    sDiagram: String = "diagram",
    sShape: String = "shape",
    sStyle: String = "style",
    sValidator: String = "validator"
) extends OFormat[GraphicalDsl] {

  override def writes(graphicalDsl: GraphicalDsl): JsObject = Json.obj(
    sId -> graphicalDsl.id,
    sName -> graphicalDsl.name,
    sConcept -> conceptFormat.writes(graphicalDsl.concept),
    sDiagram -> graphicalDsl.diagram,
    sShape -> graphicalDsl.shape,
    sStyle -> graphicalDsl.style,
    sValidator -> graphicalDsl.validator
  )

  override def reads(json: JsValue): JsResult[GraphicalDsl] = for {
    id <- (json \ sId).validate[UUID]
    name <- (json \ sName).validate[String]
    concept <- (json \ sConcept).validate(conceptFormat)
    diagram <- (json \ sDiagram).validate[String]
    shape <- (json \ sShape).validate[String]
    style <- (json \ sStyle).validate[String]
    validator <- (json \ sValidator).validateOpt[String]
  } yield {
    GraphicalDsl(id, name, concept, diagram, shape, style, validator)
  }

  def empty: Reads[GraphicalDsl] = new Reads[GraphicalDsl] {
    override def reads(json: JsValue): JsResult[GraphicalDsl] = {
      (json \ sName).validate[String].map(GraphicalDsl.empty)
    }
  }

}
