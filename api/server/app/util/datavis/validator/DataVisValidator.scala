package modigen.util.datavis.validator

import util.datavis.domain.Assignment
import util.datavis.domain.BooleanLiteral
import util.datavis.domain.Condition
import util.datavis.domain.Conditional
import util.datavis.domain.Literal
import util.datavis.domain.MIdentifier
import util.datavis.domain.NumericLiteral
import util.datavis.domain.StringLiteral
import util.datavis.domain.StyleIdentifier
import util.domain.MAttribute
import util.domain.MAttributeBoolean
import util.domain.MAttributeMEnumNumber
import util.domain.MAttributeMEnumString
import util.domain.MAttributeNumber
import util.domain.MAttributeString
import util.domain.MEnumNumber
import util.domain.MEnumString
import util.domain.ObjectWithAttributes

/**
 * DataVisValidator
 */
class DataVisValidator {
  val errors = scala.collection.mutable.MutableList[String]()

  def validate(conditionals: List[Conditional], mObj: ObjectWithAttributes): Boolean = {
    conditionals.forall(c => validate(c, mObj))
  }

  def validate(conditional: Conditional, mObj: ObjectWithAttributes): Boolean = {
    isTypeValid(conditional.condition, mObj) && isTypeValid(conditional.assignment, mObj)
  }

  private def isTypeValid(condition: Condition, mObj: ObjectWithAttributes) = condition.x match {
    case id: MIdentifier => validateIdentifier(condition, mObj, id)
    case lit: Literal => validateLiteral(condition, mObj, lit)
    case style: StyleIdentifier => true
  }

  private def validateIdentifier(condition: Condition, mObj: ObjectWithAttributes, id: MIdentifier) = {
    condition.y match {
      case id2: MIdentifier => areTypesEqual(id, id2, mObj) || fail("Comparison of incompatible attributes")
      case lit: Literal => areTypesEqual(lit, id, mObj) || fail("Comparison of attribute " + id + " with incompatible literal")
      case style: StyleIdentifier => true
    }
  }

  private def validateLiteral(condition: Condition, mObj: ObjectWithAttributes, lit: Literal) = {
    condition.y match {
      case id: MIdentifier => areTypesEqual(lit, id, mObj) || fail("Comparison of attribute " + id + " with incompatible literal")
      case lit2: Literal => if (lit.getClass == lit2.getClass) true else fail("Comparison of incompatible literals")
      case style: StyleIdentifier => true
    }
  }

  private def isTypeValid(assignment: Assignment, mObj: ObjectWithAttributes) = assignment.target match {
    case style: StyleIdentifier => true
    case mid: MIdentifier =>
      areTypesEqual(assignment.value, mid, mObj) || fail("Assignment of incompatible type for attribute " + assignment.target)
  }

  private def areTypesEqual(x: MIdentifier, y: MIdentifier, mObj: ObjectWithAttributes) = {
    mObj.attribute(x.identifier) match {
      case None => fail(mObj.name + "has no attribute " + x.identifier)
      case Some(attrX) => mObj.attribute(y.identifier) match {
        case None => fail(mObj.name + "has no attribute " + y.identifier)
        case Some(attrY) =>
          attrX._type == attrY._type || fail("Type error: types of " + x + " and " + y + " do not match. (x:" + attrX._type + ", y:" + attrY._type + ")")
      }
    }
  }

  private def areTypesEqual(literal: Literal, id: MIdentifier, mObj: ObjectWithAttributes) = mObj.attribute(id.identifier) match {
    case None => fail(mObj.name + "has no attribute " + id.identifier)
    case Some(attr) => checkMAttribute(literal, id, attr)
  }

  private def checkMAttribute(literal: Literal, id: MIdentifier, attr: MAttribute) = {
    attr match {
      case sa: MAttributeString => checkString(literal, id)
      case na: MAttributeNumber => checkNumber(literal, id)
      case ba: MAttributeBoolean => checkBoolean(literal, id)
      case sea: MAttributeMEnumString => checkEnumString(literal, id, sea)
      case nea: MAttributeMEnumNumber => checkEnumNumber(literal, id, nea)
    }
  }

  private def checkString(literal: Literal, id: MIdentifier) = {
    isString(literal) || fail("Type error: String expected for " + id)
  }

  private def isString(literal: Literal) = literal match {
    case sl: StringLiteral => true
    case _ => fail(literal + " is not a String")
  }

  private def checkNumber(literal: Literal, id: MIdentifier) = {
    isNumber(literal) || fail("Type error: Number expected for " + id)
  }

  private def isNumber(literal: Literal) = literal match {
    case nl: NumericLiteral => true
    case _ => fail(literal + " is not a number")
  }

  private def checkBoolean(literal: Literal, id: MIdentifier) = {
    isBoolean(literal) || fail("Type error: Boolean expected for " + id)
  }

  private def isBoolean(literal: Literal) = literal match {
    case sb: BooleanLiteral => true
    case _ => fail(literal + " is not a boolean")
  }

  private def checkEnumString(literal: Literal, id: MIdentifier, attr: MAttributeMEnumString) = {
    isValidEnumLiteral(literal, attr.enum) || fail("Type error: " + attr.enum.name + " expected for " + id)
  }

  private def isValidEnumLiteral(literal: Literal, mEnum: MEnumString) = literal match {
    case sl: StringLiteral =>
      mEnum.values.contains(sl.string) || fail(literal + " is not a valid value for MEnum" + mEnum.name)
    case _ => fail(literal + "is not a String")
  }

  private def checkEnumNumber(literal: Literal, id: MIdentifier, attr: MAttributeMEnumNumber) = {
    isValidEnumLiteral(literal, attr.enum) || fail("Type error: " + attr.enum.name + " expected for " + id)
  }

  private def isValidEnumLiteral(literal: Literal, mEnum: MEnumNumber) = literal match {
    case nl: NumericLiteral =>
      mEnum.values.contains(nl.double) || fail(literal + " is not a valid value for MEnum" + mEnum.name)
    case _ => fail(literal + "is not a number")
  }

  /**
   * Add message to errors
   * @param msg Error Message
   * @return Return always false
   */
  protected def fail(msg: String): Boolean = {
    errors += msg
    false
  }

}
