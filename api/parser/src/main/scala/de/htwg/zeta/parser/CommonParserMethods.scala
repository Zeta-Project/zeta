package de.htwg.zeta.parser

import scalafx.scene.paint.Color

import scala.util.Try
import scala.util.parsing.combinator.JavaTokenParsers
import scala.util.{Failure => TryFailure}
import scala.util.{Success => TrySuccess}

// scalastyle:off non.ascii.character.disallowed
trait CommonParserMethods extends JavaTokenParsers {

  // language independent
  val leftBrace = "{"
  val rightBrace = "}"
  val colon = ":"
  val leftParenthesis = "("
  val rightParenthesis = ")"
  val comma = ","
  val eq = "="

  val emptyString = ""
  val lineBreak = "\n"

  /**
   * Remove comments and trim whitespaces from input string. This method should be called
   * before a parsing process.
   * A comment is written with double slash:
   * <code>// comment</code>
   *
   * @param s raw input string which should be parsed
   * @return trimmed string without comments
   */
  def trimRight(s: String): String = {
    s.replaceAll("\\/\\/.+", emptyString).split(lineBreak).map(s => s.trim + lineBreak).mkString
  }

  def argumentDouble: Parser[Double] = "[+-]?\\d+(\\.\\d+)?".r ^^ { dou => dou.toDouble }

  def argumentInt: Parser[Int] = "[+-]?\\d+".r ^^ { dou => dou.toInt }

  def naturalNumber: Parser[Int] = "\\d+".r ^^ {
    _.toInt
  }

  def argument: Parser[String] =
    "((([a-züäöA-ZÜÄÖ]|[0-9])+(\\.([a-züäöA-ZÜÄÖ]|[0-9])+)*)|(\".*\")|([+-]?\\d+(\\.\\d+)?))".r ^^ {
      _.toString
    }

  def argumentBoolean: Parser[Boolean] = argumentBooleanTrue | argumentBooleanFalse ^^ (bool => bool)

  private def argumentBooleanTrue: Parser[Boolean] = "(false|no|n)".r ^^ (_ => false)

  private def argumentBooleanFalse: Parser[Boolean] = "(true|yes|y)".r ^^ (_ => true)

  def argumentColor: Parser[Color] = "(.+)".r.flatMap(parseColor)

  def argumentString: Parser[String] =
    stringLiteral ^^ { s => if (s.isEmpty) emptyString else s.substring(1, s.length - 1) }

  private def parseColor(colorString: String): Parser[Color] = {
    Parser { in =>
      Try(Color.valueOf(colorString)) match {
        case TrySuccess(color) => Success(color, in)
        case TryFailure(_) => Failure(s"Cannot parse color: $colorString", in)
      }
    }
  }

}
