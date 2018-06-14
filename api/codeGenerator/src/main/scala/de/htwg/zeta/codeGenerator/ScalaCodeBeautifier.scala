package de.htwg.zeta.codeGenerator

import grizzled.slf4j.Logging
import scalariform.formatter.ScalaFormatter
import scalariform.parser.ScalaParserException

object ScalaCodeBeautifier extends Logging {

  /**
   * Format the given scala source code. If code parsing failed, the
   * raw source is returned and only an error is logged.
   *
   * @param source raw scala source
   * @return formatted scala source
   */
  def format(source: String, fileName: String): String = try {
    ScalaFormatter.format(source).trim
  } catch {
    // catch exception to avoid failure on wrong generation
    // TODO should be handled in future
    case e: ScalaParserException =>
      logger.error(s"failed beautifying $fileName", e)
      source
  }

}
