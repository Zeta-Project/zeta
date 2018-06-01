package de.htwg.zeta.codeGenerator.model


/**
 * Represents the Map connection in Concept
 *
 * @param name   the name of the connection
 * @param key    the key for the entity
 * @param entity the entity this links to
 */
case class MapLink(
    name: String,
    key: String,
    entity: Entity
)
