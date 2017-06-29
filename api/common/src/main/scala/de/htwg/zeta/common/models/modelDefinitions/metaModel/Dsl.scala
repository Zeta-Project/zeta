package de.htwg.zeta.common.models.modelDefinitions.metaModel

import play.api.libs.json.Json
import play.api.libs.json.OWrites
import play.api.libs.json.Reads

// DSL definitions are just plain strings for now, should probably be changed in the future

/**
 * Style definition
 *
 * @param code the code
 */
case class Style(code: String)

object Style {
  implicit val styleReads: Reads[Style] = Json.reads[Style]
  implicit val styleWrites: OWrites[Style] = Json.writes[Style]
}

/**
 * Shape definition
 *
 * @param code the code
 */
case class Shape(code: String)

object Shape {
  implicit val shapeReads: Reads[Shape] = Json.reads[Shape]
  implicit val shapeWrites: OWrites[Shape] = Json.writes[Shape]
}

/**
 * Diagram definition
 *
 * @param code the code
 */
case class Diagram(code: String)

object Diagram {
  implicit val diagramReads: Reads[Diagram] = Json.reads[Diagram]
  implicit val diagramWrites: OWrites[Diagram] = Json.writes[Diagram]
}

/**
 * Container for DSL definitions, cam be set to none if not available
 *
 * @param diagram the diagram definition
 * @param shape   the shape definition
 * @param style   the style definition
 */
case class Dsl(
    diagram: Option[Diagram] = None,
    shape: Option[Shape] = None,
    style: Option[Style] = None)

object Dsl {
  implicit val dslReads: Reads[Dsl] = Json.reads[Dsl]
  implicit val dslWrites: OWrites[Dsl] = Json.writes[Dsl]
}
