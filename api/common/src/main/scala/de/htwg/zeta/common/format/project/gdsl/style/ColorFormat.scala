package de.htwg.zeta.common.format.project.gdsl.style

import scala.util.parsing.combinator.JavaTokenParsers

import de.htwg.zeta.common.models.project.gdsl.style.Color
import de.htwg.zeta.common.models.project.gdsl.style.Color
import play.api.libs.json.JsError
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Reads

class ColorFormat() extends Reads[Color] {

  def writes(color: Color): JsObject = {
    val Color(r, g, b, a) = color
    Json.obj(
      "r" -> r,
      "g" -> g,
      "b" -> b,
      "a" -> a,
      "rgb" -> s"rgb($r,$g,$b)",
      "rgba" -> s"rgba($r,$g,$b,$a)",
      "hex" -> "#%02x%02x%02x".format(color.r, g, b)
    )
  }

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
object ColorFormat {
  def apply(): ColorFormat = new ColorFormat()
}
