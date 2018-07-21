package de.htwg.zeta.codeGenerator

import grizzled.slf4j.Logging
import scalariform.formatter.ScalaFormatter
import scalariform.formatter.preferences.AlignParameters
import scalariform.formatter.preferences.DanglingCloseParenthesis
import scalariform.formatter.preferences.FirstArgumentOnNewline
import scalariform.formatter.preferences.FirstParameterOnNewline
import scalariform.formatter.preferences.Force
import scalariform.formatter.preferences.FormattingPreferences
import scalariform.parser.ScalaParserException

object ScalaCodeBeautifier extends Logging {

  private val preferences = FormattingPreferences()
    .setPreference(DanglingCloseParenthesis, Force)
    .setPreference(FirstParameterOnNewline, Force)
    .setPreference(AlignParameters, true)
    .setPreference(FirstArgumentOnNewline, Force)
    .setPreference(FirstParameterOnNewline, Force)

  /**
   * Format the given scala source code. If code parsing failed, the
   * raw source is returned and only an error is logged.
   *
   * @param fileName name of the file containing the source
   * @param source   raw scala source
   * @return formatted scala source
   */
  def format(fileName: String, source: String): String = {
    val strippedBreaks = source.replaceAll("[\r\n\\s]+", "\n")
    try {
      ScalaFormatter.format(strippedBreaks, preferences).trim
    } catch {
      // catch exception to avoid failure on wrong generation
      // TODO should be handled in future
      case e: ScalaParserException =>
        logger.error(s"failed beautifying $fileName", e)
        strippedBreaks
    }
  }

}
