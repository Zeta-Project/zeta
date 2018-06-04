package de.htwg.zeta.parser

import scala.util.parsing.input.CharSequenceReader

import de.htwg.zeta.parser.style.StyleParser
import de.htwg.zeta.parser.style.StyleParser
import org.scalatest.FreeSpec
import org.scalatest.Matchers

class CommentParserTest extends FreeSpec with Matchers {

  "A comment parser should" - {
    val parser = CommentParser()
    "strip out any inline or multiline comment" in {
      val input =
        """This is some
         |// hello world comment
         |stuff which // inline comment
         |is interrupted /* multiline
         |comment */ by many annoying
         |comments.""".stripMargin

      val expectedOutput =
        """This is some
         |stuff which is interrupted by many annoying
         |comments."""
          .stripMargin

      val result = parser.parseAll(parser.parsedText, input)
      result.get.text shouldBe expectedOutput
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

        val result = parser.parseAll(parser.parsedText, input).get
        val styleResult = StyleParser.parseStyles(result.text)
        styleResult.successful shouldBe true
      }
      "and should fail with correct line if following parser fails" - {
        "with sample 1" in {
          val input: String =
            """style ParentStyle { description = "Parent style" }
             |// comment 1
             |style ChildStyle extends ParentStyle { // comment 2
             |  /* description = "Style which extends a single parent style"*/
             |}""".stripMargin

          val result = parser.parseAll(parser.parsedText, input).get
          val styleResult = StyleParser.parseStyles(result.text)
          val styleResultFailure = styleResult match {
            case StyleParser.Failure(msg, pos: StyleParser.Input) =>
              val newInput = new CharSequenceReader(input, result.recalculatePosition(pos.source.toString, pos.offset))
              val failure = StyleParser.Failure(msg, newInput)
              println(failure)
              failure
          }
          styleResultFailure.successful shouldBe false
          styleResultFailure.next.offset shouldBe 185
        }
        "with sample 2" in {
          val input: String =
            """style ParentStyle { description = "Parent style" }
             | // comment 1
             |style ChildStyle extends ParentStyle { // comment 2
             |     description = "Style which extends a single parent style/* ignored */
             |}""".stripMargin

          val result = parser.parseAll(parser.parsedText, input).get
          val styleResult = StyleParser.parseStyles(result.text)
          val styleResultFailure = styleResult match {
            case StyleParser.Failure(msg, pos: StyleParser.Input) =>
              val newInput = new CharSequenceReader(input, result.recalculatePosition(pos.source.toString, pos.offset))
              val failure = StyleParser.Failure(msg, newInput)
              println(failure)
              failure
            case StyleParser.Error(msg, pos) =>
              val newInput = new CharSequenceReader(input, result.recalculatePosition(pos.source.toString, pos.offset))
              val failure = StyleParser.Failure(msg, newInput)
              println(failure)
              failure
          }
          styleResultFailure.successful shouldBe false
          styleResultFailure.next.offset shouldBe 139
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

          val result = parser.parseAll(parser.parsedText, input).get
          val styleResult = StyleParser.parseStyles(result.text)
          val styleResultFailure = styleResult match {
            case StyleParser.Failure(msg, pos: StyleParser.Input) =>
              val newInput = new CharSequenceReader(input, result.recalculatePosition(pos.source.toString, pos.offset))
              val failure = StyleParser.Failure(msg, newInput)
              println(failure)
              failure
            case StyleParser.Error(msg, pos) =>
              val newInput = new CharSequenceReader(input, result.recalculatePosition(pos.source.toString, pos.offset))
              val failure = StyleParser.Failure(msg, newInput)
              println(failure)
              failure
          }
          styleResultFailure.successful shouldBe false
          styleResultFailure.next.offset shouldBe 315
        }
      }
    }

    "fail on not closed multiline comments" in {
      val input =
        """This is some
         |/* multiline
         |comment which isn't closed."""
          .stripMargin

      val result = parser.parseAll(parser.parsedText, input)
      result.successful shouldBe false
    }
  }

}
