package de.htwg.zeta.codeGenerator.model

/**
 * Representing all Anchors in Concept
 *
 * @param team   representing TeamAnchor
 * @param period representing PeriodAnchor
 */
case class Anchor(
    name: String,
    team: Entity,
    period: Entity
)
