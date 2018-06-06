package de.htwg.zeta.codeGenerator.model

/**
 * Representing all Anchors in Concept
 *
 * @param team   representing TeamAnchor
 * @param period representing PeriodAnchor
 */
case class Anchor(
    team: Entity,
    period: Entity
)
