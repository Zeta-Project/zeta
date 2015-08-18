package modigen.util.datavis.validator

import modigen.util.datavis.domain._
import org.scalatest.{Matchers, FlatSpec}

class ConstrainedDataVisValidatorTest extends FlatSpec with Matchers{

  def validator = new ConstrainedDataVisValidator

  def validConditions = List(new Condition(BooleanLiteral(true), BooleanLiteral(true), Equal()), new Condition(BooleanLiteral(true), MIdentifier("foo"), Equal()), new Condition(MIdentifier("foo"), BooleanLiteral(true), Equal()), new Condition(MIdentifier("foo"), MIdentifier("foo"), Equal()))
  def invalidConditions = List(new Condition(StyleIdentifier("foo", ".bar"), BooleanLiteral(true), Equal()), new Condition(StyleIdentifier("foo", ".bar"), MIdentifier("foo"), Equal()), new Condition(StyleIdentifier("foo", ".bar"), StyleIdentifier("foo", ".bar"), Equal()), new Condition(BooleanLiteral(true), StyleIdentifier("foo", ".bar"), Equal()), new Condition(MIdentifier("foo"), StyleIdentifier("foo", ".bar"), Equal()))

  "The constrained DataVis Validator" should "only allow Comparison Operands that are literals or MIdentifier" in{
    val v = validator
    val bLit = BooleanLiteral(true)
    val nLit = NumericLiteral(1)
    val sLit = StringLiteral("foo")
    val mid = MIdentifier("bar")
    val style = StyleIdentifier("circle", ".foo")

    v.checkComparisonOperand(bLit) shouldBe true
    v.checkComparisonOperand(nLit) shouldBe true
    v.checkComparisonOperand(sLit) shouldBe true
    v.checkComparisonOperand(mid) shouldBe true
    v.checkComparisonOperand(style) shouldBe false
  }

  it should "only allow Conditions where the operands meet the constraints" in {
    val v = validator

    validConditions.foreach(c => v.checkComparisonOperands(c) shouldBe true)
    invalidConditions.foreach(c => v.checkComparisonOperands(c) shouldBe false)
  }

  it should "only allow style attributes as targets of assignments" in{
    val v = validator
    val mid = MIdentifier("bar")
    val style = StyleIdentifier("circle", ".fill")

    v.checkAssignmentTarget(mid) shouldBe false
    v.checkAssignmentTarget(style) shouldBe true
  }

  it should "validate the additional constraints" in {
    val v = validator
    val validAssignment = new Assignment(StyleIdentifier("circle", ".fill"), NumericLiteral(5))
    val invalidAssignment = new Assignment(MIdentifier("bar"), NumericLiteral(5))

    val fail1Pass2 = new Conditional(invalidConditions(1), validAssignment)
    val failBoth = new Conditional(invalidConditions(1), invalidAssignment)
    val pass1Fail2 = new Conditional(validConditions(1), invalidAssignment)
    val passBoth = new Conditional(validConditions(1), validAssignment)

    v.validateAgainstConstraints(fail1Pass2) shouldBe false
    v.validateAgainstConstraints(failBoth) shouldBe false
    v.validateAgainstConstraints(pass1Fail2) shouldBe false
    v.validateAgainstConstraints(passBoth) shouldBe true
  }
}
