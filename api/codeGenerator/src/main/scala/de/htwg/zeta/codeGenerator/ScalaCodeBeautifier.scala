package de.htwg.zeta.codeGenerator

import scalariform.formatter.ScalaFormatter

object ScalaCodeBeautifier {

  def format(source: String): String = ScalaFormatter.format(source).trim

}
