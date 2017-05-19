package de.htwg.zeta.server.generator.parser

import scala.util.parsing.combinator.JavaTokenParsers

/**
 * Created by julian on 03.11.15.
 * commonly used parsing methods
 */
trait CommonParserMethods extends JavaTokenParsers {
  // basic stuff
  def attribute: Parser[(String, String)] = variable ~ argument <~ ",?".r ^^ { case v ~ a => (v.toString, a.toString) }
  def variable: Parser[String] = "[a-züäöA-ZÜÄÖ]+([-_][a-züäöA-ZÜÄÖ]+)*".r <~ "\\s*".r ^^ { _.toString }
  def argument_double: Parser[Double] = "[+-]?\\d+(\\.\\d+)?".r ^^ { dou => dou.toDouble }
  def argument_int: Parser[Int] = "[+-]?\\d+".r ^^ { dou => dou.toInt }
  def argument: Parser[String] =
    "((([a-züäöA-ZÜÄÖ]|[0-9])+(\\.([a-züäöA-ZÜÄÖ]|[0-9])+)*)|(\".*\")|([+-]?\\d+(\\.\\d+)?))".r ^^ { _.toString }
  def argument_string: Parser[String] =
    "\".*\"".r ^^ { _.toString }
  def argument_classic: Parser[String] = """\s*\=\s*""".r ~> argument ^^ { _.toString }
  def argument_advanced_explicit: Parser[String] =
    """(?s)\((\w+([-_]\w+)*\s*=\s*([a-zA-ZüäöÜÄÖ]+|(\".*\")|([+-]?\d+(\.\d+)?)),?[\s\n]*)+\)""".r ^^ { _.toString }
  def argument_advanced_implicit: Parser[String] =
    """(?s)\((([a-zA-ZüäöÜÄÖ]+|(\".*\")|([+-]?\d+(\.\d+)?)),?\s*)+\)""".r ^^ { _.toString }
  def argument_wrapped: Parser[String] = "\\{[^\\{\\}]*\\}".r ^^ { _.toString }
  def arguments: Parser[String] =
    argument_classic | argument_advanced_explicit | argument_advanced_implicit | argument_wrapped
  def attributeAsString: Parser[String] = variable ~ arguments ^^ { case v ~ arg => v + arg }
  def attributePair: Parser[(String, String)] = variable ~ arguments ^^ { case v ~ a => (v, a) }

  /**
   * special cases - grammar is nasty..
   */
  def compartmentinfo_attribute: Parser[String] = {
    compartmentinfo_attribute_layout | compartmentinfo_attribute_stretching | compartmentinfo_attribute_spacing | compartmentinfo_attribute_margin |
      compartmentinfo_attribute_invisible | compartmentinfo_attribute_id
  }
  def compartmentinfo_attribute_layout: Parser[String] = {
    "layout\\s*=\\s*(fixed|vertical|horizontal|fit)".r ^^ { _.toString }
  }
  def compartmentinfo_attribute_stretching: Parser[String] = {
    "stretching\\s*\\(\\s*horizontal\\s*=\\s*(yes|y|true|no|n|false)\\s*,\\s*vertical\\s*=\\s*(yes|y|true|no|n|false)\\)".r ^^ { _.toString }
  }
  def compartmentinfo_attribute_spacing: Parser[String] = "spacing\\s*=\\s*\\d+".r ^^ { _.toString }
  def compartmentinfo_attribute_margin: Parser[String] = "margin\\s*=\\s*\\d+".r ^^ { _.toString }
  def compartmentinfo_attribute_invisible: Parser[String] = "invisible\\s*=\\s*invisible".r ^^ { _.toString }
  def compartmentinfo_attribute_id: Parser[String] = "id\\s*=\\s*.+".r ^^ { _.toString }

  /**
   * Some explicit usages
   */
  def split_compartment = "compartment\\s*[\\{]".r ~> rep(compartmentinfo_attribute) <~ "[\\}]".r ^^ { list => list }
  def position: Parser[Option[(Int, Int)]] = "[Pp]osition\\s*\\(\\s*(x=)?".r ~> argument ~ ((",\\s*(y=)?".r ~> argument) <~ ")") ^^ {
    case xarg ~ yarg => Some((xarg.toInt, yarg.toInt))
    case _ => None
  }
  def size: Parser[Option[(Int, Int)]] = "[Ss]ize\\s*\\(\\s*(width=)?".r ~> argument ~ (",\\s*(height=)?".r ~> argument) <~ ")" ^^ {
    case width ~ height => Some((width.toInt, height.toInt))
    case _ => None
  }
  def curve: Parser[Option[(Int, Int)]] = "[Cc]urve\\s*\\(\\s*(width=)?".r ~> argument ~ (",\\s*(height=)?".r ~> argument) <~ ")" ^^ {
    case width ~ height => Some((width.toInt, height.toInt))
    case _ => None
  }
  def idAsString: Parser[String] = "(id|ID)\\s*=?\\s*".r ~> argument ^^ { arg => arg }
  /**
   * takes a String and parses a boolean value out of it -> if string is yes|true|y
   * @param b the stringargument
   */
  def matchBoolean(b: String): Boolean = b match {
    case `b` if b toLowerCase () matches "yes|true|y" => true
    case _ => false
  }
}
