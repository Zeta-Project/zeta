package de.htwg.zeta.server.model.modelValidator.validator.rules

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult
import models.modelDefinitions.model.elements.{Edge, ModelElement}

trait SingleEdgeRule extends ElementsRule {

  override def check(elements: Seq[ModelElement]): Seq[ModelValidationResult] = Util.getEdges(elements).flatMap(edge => check(edge))

  def check(edge: Edge): Option[ModelValidationResult] = isValid(edge) match {
    case Some(result) => Some(ModelValidationResult(this, result, Some(edge)))
    case _ => None
  }

  def isValid(edge: Edge): Option[Boolean]

}
