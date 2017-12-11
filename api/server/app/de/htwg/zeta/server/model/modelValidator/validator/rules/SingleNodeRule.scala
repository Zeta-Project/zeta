package de.htwg.zeta.server.model.modelValidator.validator.rules

import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 *
 * A rule extending this trait will get the model node by node into its isValid method.
 * i.e. this rule will have to depend on just one node, and not spanning multiple nodes.
 */
trait SingleNodeRule extends NodesRule {

  override def check(elements: Seq[Node]): Seq[ModelValidationResult] = elements.flatMap(node => check(node))

  /**
   * Checks one node against the overridden isValid method returning a result
   * or None if rule is not applicable to this node.
   *
   * @param node The node to validate.
   * @return A model validation result or None if the rule is not applicable to this node.
   */
  def check(node: Node): Option[ModelValidationResult] = isValid(node) match {
    case Some(result) => Some(ModelValidationResult(this, result, Some(Left(node))))
    case None => None
  }

  /**
   * Validate one node.
   * If the rule is applicable to this node, it will return Some(true) or Some(false), otherwise None.
   *
   * @param node The node.
   * @return Boolean indicating if the node is valid in respect to this rule.
   */
  def isValid(node: Node): Option[Boolean]

}
