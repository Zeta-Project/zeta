package de.htwg.zeta.common.models.entity

import java.util.UUID

import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept

case class GraphicalDslRelease(
    id: UUID,
    name: String,
    concept: Concept,
    diagram: String,
    shape: String,
    style: String,
    version: String
) extends Entity
