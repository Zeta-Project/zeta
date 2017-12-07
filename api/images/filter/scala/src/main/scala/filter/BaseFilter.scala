package filter

import de.htwg.zeta.common.models.modelDefinitions.model.GraphicalDslInstance

trait BaseFilter {
  def filter(entity: GraphicalDslInstance): Boolean
}
