package de.htwg.zeta.common.models.project

import java.util.UUID

import de.htwg.zeta.common.models.entity.Entity
import de.htwg.zeta.common.models.project.concept.Concept
import play.api.libs.json.JsObject
import play.api.libs.json.Json

/** Graphical-DSL (formerly named MetaModelEntity). */
case class GdslProject(
    id: UUID,
    name: String,
    concept: Concept,
    diagram: String,
    shape: String,
    style: String,
    validator: Option[String] = None
) extends Entity {
  def asJson: JsObject = Json.obj(
    "id" -> this.id,
    "name" -> name,
    "concept" -> concept.asJson,
    "diagram" -> diagram,
    "shape" -> shape,
    "style" -> style,
    "validator" -> validator
  )
}

object GdslProject {

  def empty(name: String): GdslProject = {
    val emptyString = ""
    GdslProject(
      id = UUID.randomUUID(),
      name = name,
      concept = Concept.empty,
      diagram = emptyString,
      shape = emptyString,
      style = emptyString,
      validator = None
    )
  }

}
