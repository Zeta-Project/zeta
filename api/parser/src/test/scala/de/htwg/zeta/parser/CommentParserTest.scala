package de.htwg.zeta.parser

import de.htwg.zeta.parser.style.StyleParser
import org.scalatest.FreeSpec
import org.scalatest.Matchers

class CommentParserTest extends FreeSpec with Matchers {

  "A comment parser should" - {
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

      val parser = CommentParser()
      val result = parser.parseAll(parser.parsedText, input)
      result.get shouldBe expectedOutput
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

        val parser = CommentParser()
        val result = parser.parseAll(parser.parsedText, input).get
        StyleParser.parseStyles(result)
      }
    }

    "fail on not closed multiline comments" in {
      val input =
        """This is some
         |/* multiline
         |comment which isn't closed."""
          .stripMargin

      val parser = CommentParser()
      val result = parser.parseAll(parser.parsedText, input)
      result.successful shouldBe false
    }
  }

}
