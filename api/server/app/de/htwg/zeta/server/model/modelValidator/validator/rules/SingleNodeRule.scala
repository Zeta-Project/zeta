package de.htwg.zeta.server.model.modelValidator.validator.rules

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult
import models.modelDefinitions.model.elements.ModelElement
import models.modelDefinitions.model.elements.Node

trait SingleNodeRule extends ElementsRule {

  override def check(elements: Seq[ModelElement]): Seq[ModelValidationResult] = Util.getNodes(elements).flatMap(node => check(node))

  def check(node: Node): Option[ModelValidationResult] = isValid(node) match {
    case Some(result) => Some(ModelValidationResult(this, result, Some(node)))
    case _ => None
  }

  def isValid(node: Node): Option[Boolean]

}
