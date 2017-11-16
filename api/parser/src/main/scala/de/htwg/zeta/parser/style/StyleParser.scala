package de.htwg.zeta.parser.style

import de.htwg.zeta.server.generator.parser.CommonParserMethods
import grizzled.slf4j.Logging

/**
 */
trait StyleParser extends CommonParserMethods with Logging {

  def parseStyle(input: String): ParseResult[StyleParseModel] = parse(style, trimRight(input))

  private def trimRight(s: String): String = s.replaceAll("\\/\\/.+", "").split("\n").map(s => s.trim + "\n").mkString

  protected def style: Parser[StyleParseModel]
}
