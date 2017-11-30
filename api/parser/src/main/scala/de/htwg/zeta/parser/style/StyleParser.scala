package de.htwg.zeta.parser.style

import de.htwg.zeta.server.generator.parser.CommonParserMethods
import grizzled.slf4j.Logging

/**
 */
trait StyleParser extends CommonParserMethods with Logging {

  def parseStyles(input: String): ParseResult[List[StyleParseTree]] = parse(styles, trimRight(input))

  private def trimRight(s: String): String = s.replaceAll("\\/\\/.+", "").split("\n").map(s => s.trim + "\n").mkString

  protected def styles: Parser[List[StyleParseTree]]
}
