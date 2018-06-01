package de.htwg.zeta.codeGenerator.model

/**
 * Represents the Reference connection in Concept
 *
 * @param name       the name of this Link
 * @param entityPath the path to the Entity (beginning at the Anchor) this is referencing.
 */
case class ReferenceLink(
    name: String,
    entityPath: String
)
