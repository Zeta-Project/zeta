package de.htwg.zeta.parser.common

import scala.util.parsing.input.CharSequenceReader

import de.htwg.zeta.parser.CommonParserMethods
import de.htwg.zeta.parser.common.CommentParser.CommentResult
import de.htwg.zeta.parser.common.CommentParser.ParsedResult
import de.htwg.zeta.parser.common.CommentParser.StrippedResult
import de.htwg.zeta.parser.common.CommentParser.TextResult

class CommentParser extends CommonParserMethods {

  override def skipWhitespace: Boolean = false

  def parseComments(input: String): Either[ParseError, StrippedResult] = parseAll(parsedText(input), input) match {
    case NoSuccess(msg, next) => Left(ParseError(msg, next.offset, (next.pos.line, next.pos.column)))
    case Success(t, _) => Right(t)
  }

  private def parsedText(rawInput: String): Parser[StrippedResult] = rep(comment | text) ^^
    (b => {
      val strippedText = b.collect({ case TextResult(a) => a }).mkString
      StrippedResult(
        strippedText,
        error => {
          val failureArray = strippedText.toCharArray
          val rawArray = rawInput.toCharArray

          val (_, newPointer) = rawArray.foldLeft((0, 0))((pointer, r) => {
            val (p1, p2) = pointer
            val f = failureArray(p1)
            if(p2 < rawArray.length && p1 < failureArray.length && p1 < error.offset) {
              (p1 + (if (r == f) 1 else 0), p2 + 1)
            } else {
              pointer
            }
          })

          val newPosition = new CharSequenceReader(rawArray, newPointer)
          error.copy(offset = newPosition.offset, position = (newPosition.pos.line, newPosition.pos.column))
        }
      )
    })

  private def text: Parser[ParsedResult] = anyString ^^ (a => TextResult(a))

  private def comment: Parser[ParsedResult] = lineComment ||| multilineComment

  private def multilineComment: Parser[CommentResult] =
    """(?s)/\*.+?\*/""".r ^^ (a => CommentResult(a))

  private def lineComment: Parser[CommentResult] =
    """//[^\v]+""".r ^^ (a => CommentResult(a))

  private def anyString: Parser[String] =
    """(?s)^((?!/[/\*]).)+""".r ^^ (_.toString)

}
object CommentParser {
  def apply(): CommentParser = new CommentParser()

  trait ParsedResult {
    val text: String

    def size(): (Int, Int) = (lineIndex(text), rowIndex(text))

    def chars(): Int = text.length

    private def rowIndex(input: String): Int = input.linesIterator.toList.last.length

    private def lineIndex(input: String): Int = input.linesIterator.length
  }
  case class TextResult(text: String) extends ParsedResult
  case class CommentResult(text: String) extends ParsedResult

  case class StrippedResult(text: String, recalculatePosition: ParseError => ParseError)
}
