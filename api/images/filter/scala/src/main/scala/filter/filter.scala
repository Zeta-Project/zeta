package filter

import de.htwg.zeta.common.models.document.ModelEntity

trait BaseFilter {
  def filter(entity: ModelEntity): Boolean
}
