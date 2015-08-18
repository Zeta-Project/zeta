package modigen.util.datavis.validator

import modigen.util.datavis.domain._
import modigen.util.domain._
import org.scalatest.{Matchers, FlatSpec}

class DataVisValidatorTest extends FlatSpec with Matchers{
  def createFixture = new DataVisValidator
  def createObject = {
    val sEnum = MEnumString(List("foo"), "StringEnum")
    val nEnum = MEnumNumber(List(3), "numberEnum")

    val string = new MAttributeString("string", 0, 0)
    val number = new MAttributeNumber("number", 0, 0)
    val boolean = new MAttributeBoolean("bool", 0, 0)
    val eString = new MAttributeMEnumString(sEnum, "enumstring", 0, 0, Some("foo"))
    val eNumber = new MAttributeMEnumNumber(nEnum, "enumno", 0, 0, Some(3))

    new MClass("class", List(), List(string, number, boolean, eString, eNumber))
  }

  def validAssignment = new Assignment(MIdentifier("string"), StringLiteral("bar"))
  def invalidAssignment = new Assignment(MIdentifier("string"), NumericLiteral(3))

  def validConditions = List(new Condition(StringLiteral("foo"), StringLiteral("bar"), Equal()), new Condition(StringLiteral("foo"), MIdentifier("string"), Equal()), new Condition(MIdentifier("string"), StringLiteral("foo"), Equal()), new Condition(MIdentifier("string"), MIdentifier("string"), Equal()))
  def invalidConditions = List(new Condition(StringLiteral("foo"), NumericLiteral(3), Equal()), new Condition(NumericLiteral(3), MIdentifier("string"), Equal()), new Condition(MIdentifier("string"), NumericLiteral(3), Equal()), new Condition(MIdentifier("number"), MIdentifier("string"), Equal()))

  def validConditional = new Conditional(validConditions(1), validAssignment)

  def invalidConditionals = {
    val invalidConditional1 = new Conditional(validConditions(2), invalidAssignment)
    val invalidConditional2 = new Conditional(invalidConditions(1), validAssignment)
    val invalidConditional3 = new Conditional(invalidConditions(1), invalidAssignment)
    List(invalidConditional1, invalidConditional2, invalidConditional3)
  }

  "The Validator" should "handle failure" in {
    val validator = createFixture
    validator.fail("abc") should be(false)
    validator.errors should contain("abc")
  }

  it should "recognise valid and invalid numerical MEnum values" in {
    val validator = createFixture
    val enum = MEnumNumber(List(2, 3.4), "TestEnum")
    val valid = NumericLiteral(3.4)
    val invalid = NumericLiteral(5)

    validator.isValidEnumLiteral(valid, enum) should be(true)
    validator.errors.isEmpty should be(true)
    validator.isValidEnumLiteral(invalid, enum) should be(false)
    validator.isValidEnumLiteral(BooleanLiteral(true), enum) should be(false)
    validator.isValidEnumLiteral(StringLiteral("5"), enum) should be(false)
  }

  it should "recognise valid and invalid String MEnum values" in {
    val validator = createFixture
    val enum = MEnumString(List("TITO","XKI"), "TestEnum")
    val valid = StringLiteral("XKI")
    val invalid = StringLiteral("DEN")

    validator.isValidEnumLiteral(valid, enum) should be(true)
    validator.errors.isEmpty should be(true)
    validator.isValidEnumLiteral(invalid, enum) should be(false)
    validator.isValidEnumLiteral(BooleanLiteral(true), enum) should be(false)
    validator.isValidEnumLiteral(NumericLiteral(3), enum) should be(false)
  }

  it should "recognise literals" in {
    val validator = createFixture
    val blit = BooleanLiteral(true)
    val nlit = NumericLiteral(3)
    val slit = StringLiteral("foo")

    validator.isBoolean(blit) should be(true)
    validator.isNumber(nlit) should be(true)
    validator.isString(slit) should be(true)
    validator.errors.isEmpty should be(true)

    validator.isBoolean(nlit) should be(false)
    validator.isBoolean(slit) should be(false)
    validator.isNumber(blit) should be(false)
    validator.isNumber(slit) should be(false)
    validator.isString(blit) should be(false)
    validator.isString(nlit) should be(false)
  }

  it should "check scoping of attributes" in{
    val validator = createFixture
    val mclass = createObject
    val slit = StringLiteral("foo")
    val none = MIdentifier("none")
    val some = MIdentifier("string")

    validator.areTypesEqual(slit, none, mclass) should be(false)
    validator.areTypesEqual(none, some, mclass) should be(false)
    validator.areTypesEqual(some, none, mclass) should be(false)
  }

  it should "compare types of attributes and literals" in {
    val validator = createFixture
    val mclass = createObject
    val slit = StringLiteral("foo")
    val nlit = NumericLiteral(3)
    val blit = BooleanLiteral(true)
    val sid = MIdentifier("string")
    val nid = MIdentifier("number")
    val bid = MIdentifier("bool")
    val esid = MIdentifier("enumstring")
    val enid = MIdentifier("enumno")

    validator.areTypesEqual(slit, sid, mclass) should be(true)
    validator.areTypesEqual(slit, bid, mclass) should be (false)
    validator.areTypesEqual(slit, nid, mclass) should be (false)
    validator.areTypesEqual(slit, esid, mclass) should be(true)
    validator.areTypesEqual(slit, enid, mclass) should be(false)

    validator.areTypesEqual(nlit, sid, mclass) should be(false)
    validator.areTypesEqual(nlit, bid, mclass) should be(false)
    validator.areTypesEqual(nlit, nid, mclass) should be(true)
    validator.areTypesEqual(nlit, esid, mclass) should be(false)
    validator.areTypesEqual(nlit, enid, mclass) should be(true)

    validator.areTypesEqual(blit, sid, mclass) should be(false)
    validator.areTypesEqual(blit, bid, mclass) should be(true)
    validator.areTypesEqual(blit, nid, mclass) should be(false)
    validator.areTypesEqual(blit, esid, mclass) should be(false)
    validator.areTypesEqual(blit, enid, mclass) should be(false)
  }

  it should "compare the types of two attributes" in {
    val validator = createFixture
    val mclass = createObject
    val sid = MIdentifier("string")
    val nid = MIdentifier("number")
    val bid = MIdentifier("bool")
    val esid = MIdentifier("enumstring")
    val enid = MIdentifier("enumno")

    validator.areTypesEqual(sid, sid, mclass) should be(true)
    validator.areTypesEqual(sid, nid, mclass) should be(false)
    validator.areTypesEqual(sid, bid, mclass) should be(false)
    validator.areTypesEqual(sid, esid, mclass) should be(false)
    validator.areTypesEqual(sid, enid, mclass) should be(false)

    validator.areTypesEqual(nid, sid, mclass) should be(false)
    validator.areTypesEqual(nid, nid, mclass) should be(true)
    validator.areTypesEqual(nid, bid, mclass) should be(false)
    validator.areTypesEqual(nid, esid, mclass) should be(false)
    validator.areTypesEqual(nid, enid, mclass) should be(false)

    validator.areTypesEqual(bid, sid, mclass) should be(false)
    validator.areTypesEqual(bid, nid, mclass) should be(false)
    validator.areTypesEqual(bid, bid, mclass) should be(true)
    validator.areTypesEqual(bid, esid, mclass) should be(false)
    validator.areTypesEqual(bid, enid, mclass) should be(false)

    validator.areTypesEqual(esid, sid, mclass) should be(false)
    validator.areTypesEqual(esid, nid, mclass) should be(false)
    validator.areTypesEqual(esid, bid, mclass) should be(false)
    validator.areTypesEqual(esid, esid, mclass) should be(true)
    validator.areTypesEqual(esid, enid, mclass) should be(false)

    validator.areTypesEqual(enid, sid, mclass) should be(false)
    validator.areTypesEqual(enid, nid, mclass) should be(false)
    validator.areTypesEqual(enid, bid, mclass) should be(false)
    validator.areTypesEqual(enid, esid, mclass) should be(false)
    validator.areTypesEqual(enid, enid, mclass) should be(true)
  }

  it should "check the type validity of an assignment" in {
    val validator = createFixture
    val mclass = createObject

    validator.isTypeValid(validAssignment, mclass) should be(true)
    validator.isTypeValid(invalidAssignment, mclass) should be(false)
  }

  it should "check the type validity of a condition" in {
    def validator = createFixture
    def mclass = createObject

    validConditions.foreach(c => validator.isTypeValid(c, mclass) should be(true))
    invalidConditions.foreach(c => validator.isTypeValid(c, mclass) should be(false))
  }

  it should "check the validity of conditionals" in{
    val validator = createFixture
    val mclass = createObject

    validator.validate(validConditional, mclass) should be(true)
    invalidConditionals.foreach(c => validator.validate(c, mclass) should be(false))
  }

  it should "check the validity of lists of conditionals" in{
    val validator = createFixture
    val mclass = createObject

    validator.validate(List(validConditional), mclass) should be(true)
    validator.validate(validConditional :: invalidConditionals, mclass) should be(false)
  }
}
