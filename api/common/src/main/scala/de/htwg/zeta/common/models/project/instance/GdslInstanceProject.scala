package de.htwg.zeta.common.models.project.instance

import de.htwg.zeta.common.models.entity.GraphicalDslRelease
import de.htwg.zeta.common.models.project.GdslProject

/**
 * A Instance of a GdslInstanceProject (formaly named Model)
 * Only used for REST response.
 * Params: gDSLInstance - the instance of GraphicalDsl
 *         gDSLRelease - the instance of GraphicalDslRelease
 * @param gDSLInstance
 */
case class GdslInstanceProject(
    gDSLInstance: GraphicalDslInstance,
    gDSLProject: GdslProject
)

object GdslInstanceProject {
  def empty(gDSLInstance: GraphicalDslInstance, gDSLProject: GdslProject): GdslInstanceProject = {
    GdslInstanceProject (gDSLInstance, gDSLProject)
  }
}
