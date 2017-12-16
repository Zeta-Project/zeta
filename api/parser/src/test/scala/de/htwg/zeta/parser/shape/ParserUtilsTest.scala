package de.htwg.zeta.parser.shape

import org.scalatest.FreeSpec
import org.scalatest.Matchers

class ParserUtilsTest extends FreeSpec with Matchers with ParserUtils {

  val pointLiteral = "point"
  val widthLiteral = "width"
  val heightLiteral = "height"

  trait Attribute
  case class Point() extends Attribute
  case class Width() extends Attribute
  case class Height() extends Attribute

  private val parsePoint: Parser[Point] = pointLiteral ^^ (_ => Point())
  private val parseWidth: Parser[Width] = widthLiteral ^^ (_ => Width())
  private val parseHeight: Parser[Height] = heightLiteral ^^ (_ => Height())

  "An unordered parse will give" - {
    "a successful result when" - {
      "one point element should be found in 'point'" in {
        val conf = List(ParseConfiguration(parsePoint, 1))
        val result: ParseResult[List[Attribute]] = parse(unordered(conf), pointLiteral)
        result.successful shouldBe true
        result.get should contain(Point())
        result.get should have size 1
      }

      "one point and one width element should be found" - {
        val conf: List[ParseConfiguration[UnorderedParseResult[Attribute]]] =
          List(ParseConfiguration(parsePoint, 1), ParseConfiguration(parseWidth, 1))

        "in 'point width'" in {
          val result: ParseResult[List[Attribute]] = parse(unordered(conf), "point width")
          result.successful shouldBe true
          result.get should contain(Point())
          result.get should contain(Width())
          result.get should have size 2
        }
        "and in 'point width'" in {
          val result: ParseResult[List[Attribute]] = parse(unordered(conf), "width point")
          result.successful shouldBe true
          result.get should contain(Point())
          result.get should contain(Width())
          result.get should have size 2
        }
      }

      "one point and two width and one height elements should be found" - {
        val conf: List[ParseConfiguration[UnorderedParseResult[Attribute]]] =
          List(ParseConfiguration(parsePoint, 1), ParseConfiguration(parseWidth, 2), ParseConfiguration(parseHeight, 1))

        "in 'point width height width'" in {
          val result: ParseResult[List[Attribute]] = parse(unordered(conf), "point width height width")
          result.successful shouldBe true
          result.get should contain(Point())
          result.get.count(e => e.equals(Width())) shouldBe 2
          result.get should contain(Height())
          result.get should have size 4
        }
        "and 'width point width height'" in {
          val result: ParseResult[List[Attribute]] = parse(unordered(conf), "width point width height")
          result.successful shouldBe true
          result.get should contain(Point())
          result.get.count(e => e.equals(Width())) shouldBe 2
          result.get should contain(Height())
          result.get should have size 4
        }
        "and 'width width point height'" in {
          val result: ParseResult[List[Attribute]] = parse(unordered(conf), "width width point height")
          result.successful shouldBe true
          result.get should contain(Point())
          result.get.count(e => e.equals(Width())) shouldBe 2
          result.get should contain(Height())
          result.get should have size 4
        }
      }

    }

    "a not successful result when" - {
      "one point element should be found in 'height'" in {
        val conf = List(ParseConfiguration(parsePoint, 1))
        val result: ParseResult[List[Attribute]] = parse(unordered(conf), heightLiteral)
        result.successful shouldBe false
      }
      "one point element should be found in 'xy'" in {
        val conf = List(ParseConfiguration(parsePoint, 1))
        val result: ParseResult[List[Attribute]] = parse(unordered(conf), "xy")
        result.successful shouldBe false
      }
      "two point elements should be found in 'point point'" in {
        val conf = List(ParseConfiguration(parsePoint, 1))
        val result: ParseResult[List[Attribute]] = parse(unordered(conf), "point point")
        result.successful shouldBe false
      }
      "one point element and two height elements should be found in 'height point'" in {
        val conf: List[ParseConfiguration[UnorderedParseResult[Attribute]]] =
          List(ParseConfiguration(parsePoint, 1), ParseConfiguration(parseHeight, 2))
        val result: ParseResult[List[Attribute]] = parse(unordered(conf), "height point")
        result.successful shouldBe false
      }
    }

  }

}
