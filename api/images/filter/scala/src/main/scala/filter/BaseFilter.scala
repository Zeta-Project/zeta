package filter

import de.htwg.zeta.common.models.entity.ModelEntity

trait BaseFilter {
  def filter(entity: ModelEntity): Boolean
}
