package de.htwg.zeta.parser

import de.htwg.zeta.parser.CommentParser.CommentResult
import de.htwg.zeta.parser.CommentParser.ParsedResult
import de.htwg.zeta.parser.CommentParser.TextResult

class CommentParser extends CommonParserMethods {

  def parsedText: Parser[String] = rep(comment | text) ^^
    (_.collect({ case TextResult(a) => a }).mkString)

  private def text: Parser[ParsedResult] = anyString ^^ (a => TextResult(a))

  private def comment: Parser[ParsedResult] = lineComment | multilineComment

  private def multilineComment: Parser[CommentResult] =
    "/*" ~> anyString <~ "*/" ^^ (a => CommentResult(a))

  private def lineComment: Parser[CommentResult] =
    "//" ~> stringUntilLineBreak ^^ (a => CommentResult(a))

  private def stringUntilLineBreak: Parser[String] =
    """[^\v]+""".r ^^ (_.toString)

  private def anyString: Parser[String] =
    """[^(//)(/*)]+""".r ^^ (_.toString)

}
object CommentParser {
  def apply(): CommentParser = new CommentParser()

  trait ParsedResult
  case class TextResult(input: String) extends ParsedResult
  case class CommentResult(comment: String) extends ParsedResult
}
