package de.htwg.zeta.parser

import scala.util.parsing.input.CharSequenceReader

import de.htwg.zeta.parser.CommentParser.CommentResult
import de.htwg.zeta.parser.CommentParser.ParsedResult
import de.htwg.zeta.parser.CommentParser.StrippedResult
import de.htwg.zeta.parser.CommentParser.TextResult

class CommentParser extends CommonParserMethods {

  override def skipWhitespace: Boolean = false

  def parsedText: Parser[StrippedResult] = rep(comment | text) ^^
    (b => StrippedResult(
      b.collect({ case TextResult(a) => a }).mkString,
      b,
      (s, i) => {
        val failureArray = s.toCharArray
        val rawArray = b.map(_.text).mkString.toCharArray

        var p1 = 0
        var p2 = 0
        while(p2 < rawArray.length && p1 < failureArray.length && p1 < i) {
          val f = failureArray(p1)
          val r = rawArray(p2)
          if(r == f) {
            p1+=1
            p2+=1
          } else {
            p2+=1
          }
        }

        p2
      }
    ))

  private def text: Parser[ParsedResult] = anyString ^^ (a => TextResult(a))

  private def comment: Parser[ParsedResult] = lineComment | multilineComment

  private def multilineComment: Parser[CommentResult] =
    """/\*.+\*/""".r ^^ (a => CommentResult(a))

  private def lineComment: Parser[CommentResult] =
    """//[^\v]+""".r ^^ (a => CommentResult(a))

  private def stringUntilLineBreak: Parser[String] =
    """[^\v]+""".r ^^ (_.toString)

  private def anyString: Parser[String] =
    """[^(//)(/\*)]+""".r ^^ (_.toString)

}
object CommentParser {
  def apply(): CommentParser = new CommentParser()

  trait ParsedResult {
    val text: String

    def size(): (Int, Int) = (lineIndex(text), rowIndex(text))

    def chars(): Int = text.length

    private def rowIndex(input: String): Int = input.lines.toList.last.length

    private def lineIndex(input: String): Int = input.lines.size
  }
  case class TextResult(text: String) extends ParsedResult
  case class CommentResult(text: String) extends ParsedResult

  case class StrippedResult(text: String, structure: List[ParsedResult], recalculatePosition: (String, Int) => Int)
}
