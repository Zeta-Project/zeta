package de.htwg.zeta.common.format.project.gdsl.style

import scala.util.parsing.combinator.JavaTokenParsers

import de.htwg.zeta.common.models.project.gdsl.style.Color
import play.api.libs.json.JsError
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Reads

class ColorFormat() extends Reads[Color] {

  def writes(clazz: Color): String = s"rgba(${clazz.r},${clazz.g},${clazz.b},${clazz.alpha})"

  override def reads(json: JsValue): JsResult[Color] = {
    val parseResult = ColorParser.parseColor(json.toString)
    if (parseResult.successful) {
      JsSuccess(parseResult.getOrElse(Color.defaultColor))
    } else {
      JsError()
    }
  }

  private object ColorParser extends JavaTokenParsers {
    def parseColor(input: String): ParseResult[Color] = parseAll(parser, input.trim)

    def parser: Parser[Color] = {
      val comma = ","

      def natural_number: Parser[Int] = "\\d+".r ^^ {
        _.toInt
      }

      def argument_double: Parser[Double] = "[+-]?\\d+(\\.\\d+)?".r ^^ {
        _.toDouble
      }

      ("rgba(" ~> natural_number) ~ (comma ~> natural_number) ~
        (comma ~> natural_number) ~ (comma ~> argument_double <~ ")") ^^ {
        case r ~ g ~ b ~ alpha => Color(r, g, b, alpha)
      }
    }
  }

}
