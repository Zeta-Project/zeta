package de.htwg.zeta.parser

import scala.util.matching.Regex
import scala.util.parsing.combinator.JavaTokenParsers

object EnumParser extends JavaTokenParsers with UniteParsers {

  // parse enum values by value name
  def parseEnum[T <: Enumeration](enum: T): Parser[enum.Value] = {
    enum.values.map(value =>
      ignoreCase(value.toString) ^^^ value
    ).reduceLeftOption(_ ||| _)
      .getOrElse(throw new IllegalArgumentException("Enum must contain at least one value!"))
  }

  private def ignoreCase(string: String): Regex = {
    ("""(?i)\Q""" + string + """\E""").r
  }

}
