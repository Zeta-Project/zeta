package modigen.util.datavis.parser

import modigen.util.datavis.domain._
import org.scalatest.{FlatSpec, Matchers}


import scala.util.parsing.input.CharSequenceReader

class ManipulationsParsersTest extends FlatSpec with DataVisParsers with Matchers{

  "The Manipulation Parsers" should "parse boolean literals" in {
    implicit val parserToTest = boolean
    parsing("true") should equal(new BooleanLiteral(true))
    parsing("false") should equal (new BooleanLiteral(false))
    an [IllegalArgumentException] should be thrownBy parsing("anything else")
  }

  they should "parse string literals" in {
    implicit val parserToTest = string
    parsing("\"foobar\"") should equal(new StringLiteral("\"foobar\""))
    an [IllegalArgumentException] should be thrownBy parsing("anything that's not a string")
  }

  they should "parse numerical literals" in {
    implicit val parserToTest = number
    parsing("0") should equal(new NumericLiteral(0.0))
    parsing("1") should equal(new NumericLiteral(1.0))
    parsing("5") should equal(new NumericLiteral(5.0))
    parsing("-1") should equal(new NumericLiteral(-1.0))
    parsing("-5") should equal(new NumericLiteral(-5.0))
    parsing("2.3") should equal(new NumericLiteral(2.3))
    parsing("-2.3") should equal(new NumericLiteral(-2.3))
    an [IllegalArgumentException] should be thrownBy parsing("anything that's not a number")
  }

  they should "parse literals" in{
    implicit val parserToTest = literal
    parsing("0") should equal(new NumericLiteral(0.0))
    parsing("\"foobar\"") should equal(new StringLiteral("\"foobar\""))
    parsing("true") should equal(new BooleanLiteral(true))
    an [IllegalArgumentException] should be thrownBy parsing("Anything that's not a literal")
  }

  they should "parse dotProperties" in{
    implicit val parserToTest = dotProperty
    parsing(".bar") should equal(".bar")
    an [IllegalArgumentException] should be thrownBy parsing("Anything that doesn't start with a dot")
    an [IllegalArgumentException] should be thrownBy parsing(".3")
  }

  they should "parse sequences of dotProperties" in{
    implicit val parserToTest = dotProperties
    parsing(".bar") should equal(List(".bar"))
    parsing(".foo.bar") should equal(List(".foo", ".bar"))
    parsing(".foo.bar.lol") should equal(List(".foo", ".bar", ".lol"))
    an [IllegalArgumentException] should be thrownBy parsing("Anything that doesn't start with a dot")
    an [IllegalArgumentException] should be thrownBy parsing(".foo.3")
    an [IllegalArgumentException] should be thrownBy parsing(".3.foo")
  }

  they should "parse identifiers" in{
    implicit val parserToTest = mIdentifier
    parsing("bar") should equal(new MIdentifier("bar"))
    parsing("foo.bar") should equal(new MIdentifier("foo.bar"))
    parsing("foo.bar.lol") should equal(new MIdentifier("foo.bar.lol"))
    an [IllegalArgumentException] should be thrownBy parsing(".foo")
    an [IllegalArgumentException] should be thrownBy parsing("3.foo")
    an [IllegalArgumentException] should be thrownBy parsing("3")
  }

  they should "parse operands" in{
    implicit val parserToTest = operand
    parsing("0") should equal(new NumericLiteral(0.0))
    parsing("\"foobar\"") should equal(new StringLiteral("\"foobar\""))
    parsing("true") should equal(new BooleanLiteral(true))
    parsing("foo") should equal (new MIdentifier("foo"))
    parsing("foo.bar") should equal (new MIdentifier("foo.bar"))
  }

  they should "parse comparators" in {
    implicit val parserToTest = comparator
    parsing("==") should equal(new Equal)
    parsing("!=") should equal(new NotEqual)
    an [IllegalArgumentException] should be thrownBy parsing("anything else")
  }

  they should "parse conditional statements" in {
    implicit val parserToTest = conditional
    val x = new MIdentifier("x")
    val bc = new MIdentifier("b.c")
    val y = new MIdentifier("y")
    val five = new NumericLiteral(5)
    val assignment = new Assignment(y, five)
    val condition = new Condition(x, bc, new Equal)
    val result = parsing("if x == b.c: y = 5")
    result.condition == condition
    result.assignment == assignment
  }

  //Utility function to make sure all input has been consumed and keep everything readable
  private def parsing[T](s:String)(implicit p:Parser[T]):T = {
    val phraseParser = phrase(p)
    val input = new CharSequenceReader(s)
    phraseParser(input) match {
      case Success(t,_) => t
      case NoSuccess(msg,_) => throw new IllegalArgumentException("Could not parse '" + s + "': " + msg)
    }
  }
}