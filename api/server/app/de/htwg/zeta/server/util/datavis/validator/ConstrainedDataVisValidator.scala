package de.htwg.zeta.server.util.datavis.validator

import modigen.util.datavis.validator.DataVisValidator

import de.htwg.zeta.server.util.datavis.domain.Condition
import de.htwg.zeta.server.util.datavis.domain.Conditional
import de.htwg.zeta.server.util.datavis.domain.Identifier
import de.htwg.zeta.server.util.datavis.domain.Literal
import de.htwg.zeta.server.util.datavis.domain.MIdentifier
import de.htwg.zeta.server.util.datavis.domain.Operand
import de.htwg.zeta.server.util.datavis.domain.StyleIdentifier
import de.htwg.zeta.server.util.domain.ObjectWithAttributes

class ConstrainedDataVisValidator extends DataVisValidator {

  val svgScope = List(
    "fill",
    "fill-opacity",
    "stroke",
    "stroke-width",
    "stroke-dasharray",
    "x",
    "y",
    "height",
    "width",
    "font-size",
    "font-family",
    "font-weight",
    "cx",
    "cy",
    "rx",
    "ry"
  )

  override def validate(conditional: Conditional, mObj: ObjectWithAttributes) = {
    super.validate(conditional, mObj) && validateAgainstConstraints(conditional)
  }

  def validateAgainstConstraints(conditional: Conditional) = {
    checkComparisonOperands(conditional.condition) && checkAssignmentTarget(conditional.assignment.target)
  }

  def checkComparisonOperands(condition: Condition) = {
    checkComparisonOperand(condition.x) && checkComparisonOperand(condition.y)
  }

  def checkAssignmentTarget(target: Identifier) = target match {
    case style: StyleIdentifier => checkSvgAttributes(style)
    case mid: MIdentifier => fail("You cannot use an MAttribute as an assignment target")
  }

  def checkComparisonOperand(operand: Operand) = operand match {
    case lit: Literal => true
    case mid: MIdentifier => true
    case style: StyleIdentifier => fail("You cannot use a style attribute in the condition")
  }

  def checkSvgAttributes(styleIdentifier: StyleIdentifier) = {
    svgScope.contains(styleIdentifier.identifier) || fail("SVG Attribute " + styleIdentifier.identifier + " unknown.")
  }
}
