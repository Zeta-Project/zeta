package filter

import models.document.ModelEntity

trait BaseFilter {
  def filter(entity: ModelEntity): Boolean
}