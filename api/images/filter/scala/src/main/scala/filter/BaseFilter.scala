package filter

import models.entity.ModelEntity

trait BaseFilter {
  def filter(entity: ModelEntity): Boolean
}
