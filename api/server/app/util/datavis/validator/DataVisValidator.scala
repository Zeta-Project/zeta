package modigen.util.datavis.validator

import util.datavis.domain._
import util.domain._

class DataVisValidator {
  val errors = scala.collection.mutable.MutableList[String]()

  def validate(conditionals: List[Conditional], mObj: ObjectWithAttributes): Boolean = conditionals.forall(c => validate(c, mObj))

  def validate(conditional: Conditional, mObj: ObjectWithAttributes) = isTypeValid(conditional.condition, mObj) && isTypeValid(conditional.assignment, mObj)

  def isTypeValid(condition: Condition, mObj: ObjectWithAttributes) = condition.x match {
    case id: MIdentifier => condition.y match {
      case id2: MIdentifier => areTypesEqual(id, id2, mObj) || fail("Comparison of incompatible attributes")
      case lit: Literal => areTypesEqual(lit, id, mObj) || fail("Comparison of attribute " + id + " with incompatible literal")
      case style: StyleIdentifier => true
    }
    case lit: Literal => condition.y match {
      case id: MIdentifier => areTypesEqual(lit, id, mObj) || fail("Comparison of attribute " + id + " with incompatible literal")
      case lit2: Literal => if (lit.getClass == lit2.getClass) true else fail("Comparison of incompatible literals")
      case style: StyleIdentifier => true
    }
    case style: StyleIdentifier => true
  }

  def isTypeValid(assignment: Assignment, mObj: ObjectWithAttributes) = assignment.target match {
    case style: StyleIdentifier => true
    case mid: MIdentifier => areTypesEqual(assignment.value, mid, mObj) || fail("Assignment of incompatible type for attribute " + assignment.target)
  }

  def areTypesEqual(x: MIdentifier, y: MIdentifier, mObj: ObjectWithAttributes) = {
    mObj.attribute(x.identifier) match {
      case None => fail(mObj.name + "has no attribute " + x.identifier)
      case Some(attrX) => mObj.attribute(y.identifier) match {
        case None => fail(mObj.name + "has no attribute " + y.identifier)
        case Some(attrY) => attrX._type == attrY._type || fail("Type error: types of " + x + " and " + y + " do not match. (x:" + attrX._type + ", y:" + attrY._type + ")")
      }
    }
  }

  def areTypesEqual(literal: Literal, id: MIdentifier, mObj: ObjectWithAttributes) = mObj.attribute(id.identifier) match {
    case None => fail(mObj.name + "has no attribute " + id.identifier)
    case Some(attr) => attr match {
      case sa: MAttributeString => isString(literal) || fail("Type error: String expected for " + id)
      case na: MAttributeNumber => isNumber(literal) || fail("Type error: Number expected for " + id)
      case ba: MAttributeBoolean => isBoolean(literal) || fail("Type error: Boolean expected for " + id)
      case sea: MAttributeMEnumString => isValidEnumLiteral(literal, sea.enum) || fail("Type error: " + sea.enum.name + " expected for " + id)
      case nea: MAttributeMEnumNumber => isValidEnumLiteral(literal, nea.enum) || fail("Type error: " + nea.enum.name + " expected for " + id)
    }
  }

  def isNumber(literal: Literal) = literal match {
    case nl: NumericLiteral => true
    case _ => fail(literal + " is not a number")
  }

  def isString(literal: Literal) = literal match {
    case sl: StringLiteral => true
    case _ => fail(literal + " is not a String")
  }

  def isBoolean(literal: Literal) = literal match {
    case sb: BooleanLiteral => true
    case _ => fail(literal + " is not a boolean")
  }

  def isValidEnumLiteral(literal: Literal, mEnum: MEnumString) = literal match {
    case sl: StringLiteral => mEnum.values.contains(sl.string) || fail(literal + " is not a valid value for MEnum" + mEnum.name)
    case _ => fail(literal + "is not a String")
  }

  def isValidEnumLiteral(literal: Literal, mEnum: MEnumNumber) = literal match {
    case nl: NumericLiteral => mEnum.values.contains(nl.double) || fail(literal + " is not a valid value for MEnum" + mEnum.name)
    case _ => fail(literal + "is not a number")
  }

  def fail(msg: String) = {
    errors += msg
    false
  }

}