package de.htwg.zeta.server.model.modelValidator.validator.rules

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult
import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.ModelElement

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 *
 * A rule extending this trait will get the model edge by edge into its isValid method.
 * i.e. this rule will have to depend on just one edge, and not spanning multiple edges.
 */
trait SingleEdgeRule extends ElementsRule {

  override def check(elements: Seq[ModelElement]): Seq[ModelValidationResult] = Util.getEdges(elements).flatMap(edge => check(edge))

  /**
   * Checks one edge against the overridden isValid method returning a result
   * or None if rule is not applicable to this edge.
   *
   * @param edge The edge to validate.
   * @return A model validation result or None if the rule is not applicable to this edge.
   */
  def check(edge: Edge): Option[ModelValidationResult] = isValid(edge) match {
    case Some(result) => Some(ModelValidationResult(this, result, Some(edge)))
    case _ => None
  }

  /**
   * Validate one edge.
   * If the rule is applicable to this edge, it will return Some(true) or Some(false), otherwise None.
   *
   * @param edge The edge.
   * @return Boolean indicating if the edge is valid in respect to this rule.
   */
  def isValid(edge: Edge): Option[Boolean]

}
