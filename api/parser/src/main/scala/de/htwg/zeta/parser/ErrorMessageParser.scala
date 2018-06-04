package de.htwg.zeta.parser

class ErrorMessageParser extends CommonParserMethods {

  def parseError(input: String): ParseResult[(Int, Int)] = parse(error, input)

  private def error: Parser[(Int, Int)] = {
    "[" ~> naturalNumber ~ ("." ~> naturalNumber) <~ "]" <~ """.*""".r ^^ {
      case r ~ c => (r, c)
    }
  }

}
object ErrorMessageParser {
  def apply(): ErrorMessageParser = new ErrorMessageParser()
}
