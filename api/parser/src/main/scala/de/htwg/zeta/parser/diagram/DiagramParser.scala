package de.htwg.zeta.parser.diagram

import de.htwg.zeta.server.generator.parser.CommonParserMethods

object DiagramParser extends CommonParserMethods {

  private val leftBrace = "{"
  private val rightBrace = "}"

  def parseDiagrams(input: String): ParseResult[Seq[DiagramParseTree]] = {
    parse(diagrams, input)
  }

  private def diagrams: Parser[Seq[DiagramParseTree]] = rep1(diagram)

  private def diagram: Parser[DiagramParseTree] = {
    diagramName ~ leftBrace ~ palettes ~ rightBrace ^^ { parseResult =>
      val diagramName ~ _ ~ palettes ~ _ = parseResult
      DiagramParseTree(diagramName, palettes)
    }
  }

  private def palettes: Parser[Seq[PaletteParseTree]] = rep(palette)

  private def palette: Parser[PaletteParseTree] = {
    paletteName ~ leftBrace ~ nodes ~ rightBrace ^^ { parseResult =>
      val paletteName ~ _ ~ nodes ~ _ = parseResult
      PaletteParseTree(paletteName, nodes)
    }
  }

  private def nodes: Parser[Seq[String]] = rep(node)

  private def node: Parser[String] = stringLiteral

  private def diagramName = literal("diagram") ~> ident

  private def paletteName = literal("palette") ~> ident

}
