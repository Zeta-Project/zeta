package util.datavis.parser

import scala.util.parsing.combinator.JavaTokenParsers

import util.datavis.domain.Assignment
import util.datavis.domain.BooleanLiteral
import util.datavis.domain.Comparator
import util.datavis.domain.Condition
import util.datavis.domain.Conditional
import util.datavis.domain.Equal
import util.datavis.domain.Greater
import util.datavis.domain.GreaterOrEqual
import util.datavis.domain.Less
import util.datavis.domain.LessOrEqual
import util.datavis.domain.Literal
import util.datavis.domain.MIdentifier
import util.datavis.domain.NotEqual
import util.datavis.domain.NumericLiteral
import util.datavis.domain.StringLiteral
import util.datavis.domain.StyleIdentifier

/**
 * DataVisParsers
 */
trait DataVisParsers extends JavaTokenParsers {
  protected def script = conditional.*

  private def conditional = "if" ~ condition ~ ":" ~ assignment ^^ { case _ ~ cond ~ _ ~ assign => new Conditional(cond, assign) }

  private def condition = operand ~ comparator ~ operand ^^ { case opA ~ comp ~ opB => new Condition(opA, opB, comp) }
  private def assignment = identifier ~ "=" ~ literal ^^ { case target ~ _ ~ value => new Assignment(target, value) }

  private def comparator: Parser[Comparator] = ("==" | "!=" | ">=" | "<=" | ">" | "<") ^^ {
    case "==" => new Equal
    case "!=" => new NotEqual
    case ">=" => new GreaterOrEqual
    case "<=" => new LessOrEqual
    case ">" => new Greater
    case "<" => new Less
  }

  private def operand = literal | identifier

  private def identifier = styleIdentifier | mIdentifier // Order is important!

  private def styleIdentifier = {
    "style" ~ selector ~ dotProperties ^^ {
      case _ ~ selector ~ properties => StyleIdentifier(selector, buildIdentifier("", properties))
    }
  }
  private def mIdentifier = ident ~ dotProperties ^^ { case first ~ properties => MIdentifier(buildIdentifier(first, properties)) }

  private def dotProperties = dotProperty.*
  private def dotProperty = "." ~ property ^^ { case dot ~ prop => dot + prop }
  private def property = "[_A-Za-z$][\\- _A-Za-z0-9$]*".r ^^ { s => s }

  private def selector = "\\[.*?\\]".r ^^ { selector => selector.replace("['", "").replace("']", "") }

  private def literal: Parser[Literal] = number | string | boolean
  private def number = floatingPointNumber ^^ { s => NumericLiteral(s.toDouble) }
  private def string = stringLiteral ^^ { s => StringLiteral(s) }
  private def boolean = ("true" | "false") ^^ { s => BooleanLiteral(s.toBoolean) }

  private def buildIdentifier(first: String, list: List[String]) = {
    val identifier = list.foldLeft(first) { (id, next) => id + next }
    identifier
  }
}
