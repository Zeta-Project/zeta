package de.htwg.zeta.parser

import javafx.scene.paint.Color

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

  def variable: Parser[String] = "[a-züäöA-ZÜÄÖ]+([-_][a-züäöA-ZÜÄÖ]+)*".r <~ "\\s*".r ^^ {
    _.toString
  }

  def argument_double: Parser[Double] = "[+-]?\\d+(\\.\\d+)?".r ^^ { dou => dou.toDouble }

  def argument_int: Parser[Int] = "[+-]?\\d+".r ^^ { dou => dou.toInt }

  def natural_number: Parser[Int] = "\\d+".r ^^ {
    _.toInt
  }

  def argument: Parser[String] =
    "((([a-züäöA-ZÜÄÖ]|[0-9])+(\\.([a-züäöA-ZÜÄÖ]|[0-9])+)*)|(\".*\")|([+-]?\\d+(\\.\\d+)?))".r ^^ {
      _.toString
    }

  def argument_boolean: Parser[Boolean] = argument_boolean_true | argument_boolean_false ^^ (bool => bool)

  private def argument_boolean_true: Parser[Boolean] = "(false|no|n)".r ^^ (_ => false)

  private def argument_boolean_false: Parser[Boolean] = "(true|yes|y)".r ^^ (_ => true)

  def argument_color: Parser[Color] = "(.+)".r.flatMap(parseColor)

  def argument_string: Parser[String] =
    stringLiteral ^^ { s => if (s.isEmpty) "" else s.substring(1, s.length - 1) }

  private def parseColor(colorString: String): Parser[Color] = {
    Parser { in =>
      Try(Color.valueOf(colorString)) match {
        case TrySuccess(color) => Success(color, in)
        case TryFailure(_) => Failure(s"Cannot parse color: $colorString", in)
      }
    }
  }

}
