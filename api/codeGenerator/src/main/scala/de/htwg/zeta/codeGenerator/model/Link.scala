package de.htwg.zeta.codeGenerator.model

/**
 * Represents the Link connection in Concept
 *
 * @param name   the name of this Link
 * @param entity the Entity this links to
 */
case class Link(
    name: String,
    entity: Entity
)
