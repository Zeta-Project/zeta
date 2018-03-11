package de.htwg.zeta.common.models.project

import java.util.UUID

import de.htwg.zeta.common.models.entity.Entity
import de.htwg.zeta.common.models.project.concept.Concept

/** Graphical-DSL (formerly named MetaModelEntity). */
case class GdslProject(
    id: UUID,
    name: String,
    concept: Concept,
    diagram: String,
    shape: String,
    style: String,
    validator: Option[String] = None
) extends Entity

object GdslProject {

  def empty(name: String): GdslProject = GdslProject(
    id = UUID.randomUUID(),
    name = name,
    concept = Concept.empty,
    diagram = "",
    shape = "",
    style = "",
    validator = None
  )

}
