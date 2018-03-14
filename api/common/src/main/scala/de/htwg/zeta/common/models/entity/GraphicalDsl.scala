package de.htwg.zeta.common.models.entity

import java.util.UUID

import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept

/** Graphical-DSL (formerly named MetaModelEntity). */
case class GraphicalDsl(
    id: UUID,
    name: String,
    concept: Concept,
    diagram: String,
    shape: String,
    style: String,
    validator: Option[String] = None
) extends Entity

object GraphicalDsl {

  def empty(name: String): GraphicalDsl = GraphicalDsl(
    id = UUID.randomUUID(),
    name = name,
    concept = Concept.empty,
    diagram = "",
    shape = "",
    style = "",
    validator = None
  )

}