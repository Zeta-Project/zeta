package de.htwg.zeta.common.models.modelDefinitions.metaModel

import play.api.libs.json.Json
import play.api.libs.json.Format

// DSL definitions are just plain strings for now, should probably be changed in the future

/**
 * Style definition
 *
 * @param code the code
 */
case class Style(code: String)

object Style {
  implicit val styleFormat: Format[Style] = Json.format[Style]
}

/**
 * Shape definition
 *
 * @param code the code
 */
case class Shape(code: String)

object Shape {
  implicit val shapeFormat: Format[Shape] = Json.format[Shape]
}

/**
 * Diagram definition
 *
 * @param code the code
 */
case class Diagram(code: String)

object Diagram {
  implicit val diagramFormat: Format[Diagram] = Json.format[Diagram]
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
  implicit val dslFormat: Format[Dsl] = Json.format[Dsl]
}
