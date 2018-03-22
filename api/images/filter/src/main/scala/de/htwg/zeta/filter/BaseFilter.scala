package de.htwg.zeta.filter

import de.htwg.zeta.common.models.project.instance.GraphicalDslInstance

trait BaseFilter {
  def filter(entity: GraphicalDslInstance): Boolean
}
