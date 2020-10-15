package de.htwg.zeta.parser.common

import de.htwg.zeta.parser.style.StyleParser
import org.scalatest.matchers.should.Matchers
import org.scalatest.freespec.AnyFreeSpec

class CommentParserTest extends AnyFreeSpec with Matchers {

  "A comment parser should" - {
    val parser = CommentParser()
    "strip out any inline or multiline comment" in {
      val input =
        """This is some
         |// hello world comment
         |stuff which// inline comment
         |is interrupted /* multiline
         |comment */ by many annoying
         |comments.""".stripMargin

      val expectedOutput =
        """This is some
         |
         |stuff which
         |is interrupted  by many annoying
         |comments.""".stripMargin

      val value = parser.parseComments(input)
      value.isRight shouldBe true
      val result = value.right.toOption
      result match {
        case Some(v) => v.text shouldBe expectedOutput
        case None => None shouldBe Some
      }
    }

    "append this parser in front of other parsers" - {
      "and success if all is correct" in {
        val input: String =
          """
           |style ParentStyle { description = "Parent style" }
           |// comment 1
           |style ChildStyle extends ParentStyle { // comment 2
           |  description = "Style which extends a single parent style"
           |  /* comment 3 */
           |}
          """.stripMargin
        val result = parser.parseComments(input).right.toOption match {
          case Some(v) => StyleParser.parseStyles(v.text).isRight
          case None => false
        }
        result shouldBe true
      }
      "and should fail with correct line if following parser fails" - {
        "with sample 1" in {
          val input: String =
            """style ParentStyle { description = "Parent style" }
             |// comment 1
             |style ChildStyle extends ParentStyle { // comment 2
             |  /* description = "Style which extends a single parent style"*/
             |}""".stripMargin
          val styleResult = StyleParser.parseStyles(input)
          styleResult.isLeft shouldBe true
          styleResult match {
            case Left(v) =>
              v.position shouldBe Tuple2(3,1)
            case Right(v) => v shouldBe false
          }
        }
        "with sample 2" in {
          val input: String =
            """style ParentStyle { description = "Parent style" }
             | // comment 1
             |style ChildStyle extends ParentStyle { // comment 2
             |     description = "Style which extends a single parent style/* ignored */
             |}""".stripMargin
          val styleResult = StyleParser.parseStyles(input)
          styleResult.isRight shouldBe false
          styleResult.left.toOption match {
            case Some(v) =>
              v.position shouldBe Tuple2(3,1)
            case None => None shouldBe Some
          }
        }
        "with sample 3" in {
          val input: String =
            """style ParentStyle { description = /* really ugly comment */ "Parent style" }
             | /* pre comment with some space afterwards */      // comment 1
             |style ChildStyle extends ParentStyle { // comment 2
             |  description = "Style which /* really ugly comment */ extends"
             |     line-color = red/* ignored */
             |     line-width = "a"
             |  // a comment after failing line
             |     // and another one
             |}""".stripMargin

          val styleResult = StyleParser.parseStyles(input)
          styleResult.isRight shouldBe false
          styleResult.left.toOption match {
            case Some(v) =>
              v.position shouldBe Tuple2(3,1)
            case None => None shouldBe Some
          }
        }
        "with sample 4" in {
          val input: String =
            """style ParentStyle { description = "Parent style" }
             |// comment 1
             |style FirstChildStyle extends ParentStyle { // comment 2
             |  description = "Style which extends a single parent style"
             |}
             |style SecChildStyle extends ParentStyle { // comment 2
             |  description = "Style which extends a single parent style"
             |  line-color  = red
             |  line-color = "blue"
             |}""".stripMargin
          val styleResult = StyleParser.parseStyles(input)
          styleResult match {
            case Left(v) =>
              v.position shouldBe Tuple2(6,1)
            case _ => false shouldBe true
          }
        }
      }
    }

    "fail on not closed multiline comments" in {
      val input =
        """This is some
         |/* multiline
         |comment which isn't closed."""
          .stripMargin

      val result = parser.parseComments(input)
      result.isLeft shouldBe true
    }
  }

}
