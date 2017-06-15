package de.htwg.zeta.common.models.modelDefinitions.metaModel

import play.api.libs.json.Json

// DSL definitions are just plain strings for now, should probably be changed in the future

/**
 * Style definition
 * @param code the code
 */
case class Style(code: String)

object Style {
  implicit val styleReads = Json.reads[Style]
  implicit val styleWrites = Json.writes[Style]
}

/**
 * Shape definition
 * @param code the code
 */
case class Shape(code: String)

object Shape {
  implicit val shapeReads = Json.reads[Shape]
  implicit val shapeWrites = Json.writes[Shape]
}

/**
 * Diagram definition
 * @param code the code
 */
case class Diagram(code: String)

object Diagram {
  implicit val diagramReads = Json.reads[Diagram]
  implicit val diagramWrites = Json.writes[Diagram]
}

/**
 * Container fpr DSL definitions, cam be set to none if not available
 * @param diagram the diagram definition
 * @param shape the shape definition
 * @param style the style definition
 */
case class Dsl(
    diagram: Option[Diagram] = None,
    shape: Option[Shape] = None,
    style: Option[Style] = None)

object Dsl {
  implicit val dslReads = Json.reads[Dsl]
  implicit val dslWrites = Json.writes[Dsl]
}
