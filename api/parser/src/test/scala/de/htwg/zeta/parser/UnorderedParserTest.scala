package de.htwg.zeta.parser

import scala.util.parsing.combinator.JavaTokenParsers

import org.scalactic.source
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

//noinspection ScalaStyle
class UnorderedParserTest extends AnyFreeSpec with Matchers with UnorderedParser with JavaTokenParsers {

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

  // FIX for scala compiler problem. Compiler could not find correct method to apply to String followed by `-`
  // Define implicit wrapper directly in class to fix.
  override implicit def convertToFreeSpecStringWrapper(s: String)(implicit pos: source.Position): FreeSpecStringWrapper = new FreeSpecStringWrapper(s, pos)

  "An unordered parse will give " - {
    "a successful result when " - {
      "min one point element and max three point elements should be found " - {
        val parser: Parser[List[Attribute]] = unordered(range(1, 3, parsePoint))

        "in 'point'" in {
          val result: ParseResult[List[Attribute]] = parse(parser, pointLiteral)
          result.successful shouldBe true
          result.get should contain(Point())
          result.get should have size 1
        }
        "in 'point point'" in {
          val result: ParseResult[List[Attribute]] = parse(parser, "point point")
          result.successful shouldBe true
          result.get.count(e => e.equals(Point())) shouldBe 2
          result.get should have size 2
        }
        "in 'point point point'" in {
          val result: ParseResult[List[Attribute]] = parse(parser, "point point point")
          result.successful shouldBe true
          result.get.count(e => e.equals(Point())) shouldBe 3
          result.get should have size 3
        }
      }

      "exact one point and one width element should be found" - {
        val parser: Parser[List[Attribute]] = unordered(exact(1, parsePoint), exact(1, parseWidth))

        "in 'point width'" in {
          val result: ParseResult[List[Attribute]] = parse(parser, "point width")
          result.successful shouldBe true
          result.get should contain(Point())
          result.get should contain(Width())
          result.get should have size 2
        }
        "and in 'point width'" in {
          val result: ParseResult[List[Attribute]] = parse(parser, "width point")
          result.successful shouldBe true
          result.get should contain(Point())
          result.get should contain(Width())
          result.get should have size 2
        }
      }

      "one point and min one width and max one height elements should be found" - {
        val parser: Parser[List[Attribute]] = unordered(exact(1, parsePoint), max(1, parseHeight), min(1, parseWidth))

        "in 'point width height width'" in {
          val result: ParseResult[List[Attribute]] = parse(parser, "point width height width")
          result.successful shouldBe true
          result.get should contain(Point())
          result.get.count(e => e.equals(Width())) shouldBe 2
          result.get should contain(Height())
          result.get should have size 4
        }

        "in 'point width height width' NEW" in {
          val result: ParseResult[List[Attribute]] = parse(parser, "point width height width")
          result.successful shouldBe true
          result.get should contain(Point())
          result.get.count(e => e.equals(Width())) shouldBe 2
          result.get should contain(Height())
          result.get should have size 4
        }
        "and 'width point width height'" in {
          val result: ParseResult[List[Attribute]] = parse(parser, "width point width height")
          result.successful shouldBe true
          result.get should contain(Point())
          result.get.count(e => e.equals(Width())) shouldBe 2
          result.get should contain(Height())
          result.get should have size 4
        }
        "and 'width width point height'" in {
          val result: ParseResult[List[Attribute]] = parse(parser, "width width point height")
          result.successful shouldBe true
          result.get should contain(Point())
          result.get.count(e => e.equals(Width())) shouldBe 2
          result.get should contain(Height())
          result.get should have size 4
        }
        "and 'width point'" in {
          val result: ParseResult[List[Attribute]] = parse(parser, "width point")
          result.successful shouldBe true
          result.get should contain(Point())
          result.get should contain(Width())
          result.get should have size 2
        }
        "and 'width width width point'" in {
          val result: ParseResult[List[Attribute]] = parse(parser, "width width width point")
          result.successful shouldBe true
          result.get should contain(Point())
          result.get.count(e => e.equals(Width())) shouldBe 3
          result.get should have size 4
        }
      }

    }

    "a not successful result when " - {
      "exact one point element should be found " - {
        val parser: Parser[List[Attribute]] = unordered(exact(1, parsePoint))

        "in 'height'" in {
          val result: ParseResult[List[Attribute]] = parse(parser, heightLiteral)
          result.successful shouldBe false
        }
        "in 'point point'" in {
          val result: ParseResult[List[Attribute]] = parse(parser, "point point")
          result.successful shouldBe false
        }
        "in ''" in {
          val result: ParseResult[List[Attribute]] = parse(parser, "")
          result.successful shouldBe false
        }
      }
      "minimum two point elements should be found " - {
        val parser: Parser[List[Attribute]] = unordered(min(2, parsePoint))

        "in 'point'" in {
          val result: ParseResult[List[Attribute]] = parse(parser, pointLiteral)
          result.successful shouldBe false
        }
        "in ''" in {
          val result: ParseResult[List[Attribute]] = parse(parser, "")
          result.successful shouldBe false
        }
      }
      "maximum two point elements should be found " - {
        val parser: Parser[List[Attribute]] = unordered(max(2, parsePoint))

        "in 'point point point'" in {
          val result: ParseResult[List[Attribute]] = parse(parser, "point point point")
          result.successful shouldBe false
        }
      }
      "min two and max three point elements should be found " - {
        val parser: Parser[List[Attribute]] = unordered(range(2, 3, parsePoint))

        "in 'point'" in {
          val result: ParseResult[List[Attribute]] = parse(parser, pointLiteral)
          result.successful shouldBe false
        }
        "in 'point point point point'" in {
          val result: ParseResult[List[Attribute]] = parse(parser, "point point point point")
          result.successful shouldBe false
        }
      }
      "min two and max three point elements with exact one height element should be found " - {
        val parser: Parser[List[Attribute]] = unordered(range(2, 3, parsePoint), exact(1, parseHeight))

        "in 'point height'" in {
          val result: ParseResult[List[Attribute]] = parse(parser, "point height")
          result.successful shouldBe false
        }
        "in 'point point point point height'" in {
          val result: ParseResult[List[Attribute]] = parse(parser, "point point point point height")
          result.successful shouldBe false
        }
        "in 'point point point'" in {
          val result: ParseResult[List[Attribute]] = parse(parser, "point point point")
          result.successful shouldBe false
        }
        "in 'point point point height height'" in {
          val result: ParseResult[List[Attribute]] = parse(parser, "point point point height height")
          result.successful shouldBe false
        }
      }
    }

    "if the unordered parse method is not correctly used by the developer and " - {
      "a parser is configured twice with different methods " - {
        an[IllegalArgumentException] should be thrownBy {
          unordered(max(1, parsePoint), min(1, parsePoint))
        }
      }
      "a parser is configured twice with same methods " - {
        an[IllegalArgumentException] should be thrownBy {
          unordered(exact(1, parsePoint), exact(1, parsePoint))
        }
      }
      "a parser has a negative minimum" - {
        an[IllegalArgumentException] should be thrownBy {
          unordered(exact(-2, parsePoint))
        }
      }
      "a parser has a negative maximum" - {
        an[IllegalArgumentException] should be thrownBy {
          unordered(exact(-2, parsePoint))
        }
      }
      "a parser has a minimum which is higher than the maximum" - {
        an[IllegalArgumentException] should be thrownBy {
          unordered(range(2, 1, parsePoint))
        }
      }
      "a parser has an invalid maximum" - {
        an[IllegalArgumentException] should be thrownBy {
          unordered(max(0, parsePoint))
        }
      }
    }
  }

}
