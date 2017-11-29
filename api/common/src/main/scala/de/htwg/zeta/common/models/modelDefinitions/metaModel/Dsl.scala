package de.htwg.zeta.common.models.modelDefinitions.metaModel

// DSL definitions are just plain strings for now, should probably be changed in the future

/**
 * Style definition
 *
 * @param code the code
 */
case class Style(code: String)


/**
 * Shape definition
 *
 * @param code the code
 */
case class Shape(code: String)


/**
 * Diagram definition
 *
 * @param code the code
 */
case class Diagram(code: String)

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
    style: Option[Style] = None
)
