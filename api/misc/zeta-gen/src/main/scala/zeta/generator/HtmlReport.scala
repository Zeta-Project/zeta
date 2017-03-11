package zeta.generator

import zeta.generator.domain.model.Model
import zeta.generator.transformation.{TransformationSuccess, TransformationResult, Transformation}

class GenericReport extends Transformation {
  override def transform(input: Model): TransformationResult = {
    println("output.txt")
    TransformationSuccess
  }
}
