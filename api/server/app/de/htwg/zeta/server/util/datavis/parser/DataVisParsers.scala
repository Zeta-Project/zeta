package de.htwg.zeta.server.util.datavis.parser

import scala.util.parsing.combinator.JavaTokenParsers

import de.htwg.zeta.server.util.datavis.domain.Assignment
import de.htwg.zeta.server.util.datavis.domain.BooleanLiteral
import de.htwg.zeta.server.util.datavis.domain.Comparator
import de.htwg.zeta.server.util.datavis.domain.Condition
import de.htwg.zeta.server.util.datavis.domain.Conditional
import de.htwg.zeta.server.util.datavis.domain.Equal
import de.htwg.zeta.server.util.datavis.domain.Greater
import de.htwg.zeta.server.util.datavis.domain.GreaterOrEqual
import de.htwg.zeta.server.util.datavis.domain.Less
import de.htwg.zeta.server.util.datavis.domain.LessOrEqual
import de.htwg.zeta.server.util.datavis.domain.Literal
import de.htwg.zeta.server.util.datavis.domain.MIdentifier
import de.htwg.zeta.server.util.datavis.domain.NotEqual
import de.htwg.zeta.server.util.datavis.domain.NumericLiteral
import de.htwg.zeta.server.util.datavis.domain.StringLiteral
import de.htwg.zeta.server.util.datavis.domain.StyleIdentifier

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
