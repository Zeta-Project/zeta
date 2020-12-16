package de.htwg.zeta.common.models.project.instance

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.gdsl.diagram.Diagram
import de.htwg.zeta.common.models.project.gdsl.shape.Shape
import de.htwg.zeta.common.models.project.gdsl.style.Style

/**
 * A Instance of a GdslInstanceProject (formaly named Model)
 * Only used for REST response.
 * Params: gDSLInstance - the instance of GraphicalDsl
 *         gDSLRelease - the instance of GraphicalDslRelease
 * @param gDSLInstance
 */
case class GdslInstanceProject(
    gDSLInstance: GraphicalDslInstance,
    concept: Concept,
    shape: Shape,
    diagram: List[Diagram],
    style: List[Style]
)

object GdslInstanceProject {
  def empty(gDSLInstance: GraphicalDslInstance, concept: Concept,shape: Shape, diagram: List[Diagram], style: List[Style]): GdslInstanceProject = {
    GdslInstanceProject (gDSLInstance, concept,shape,diagram,style)
  }
}
