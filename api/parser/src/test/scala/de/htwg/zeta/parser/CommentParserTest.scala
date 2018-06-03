package de.htwg.zeta.parser

import org.scalatest.FreeSpec
import org.scalatest.Matchers

class CommentParserTest extends FreeSpec with Matchers {

  "A comment parser should" - {
    "strip out any inline or multiline comment" in {
      val input = """
       |This is some
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
  }

}
