package de.htwg.zeta.parser.diagram

import de.htwg.zeta.server.generator.parser.CommonParserMethods

object DiagramParser extends CommonParserMethods {

  def parseDiagrams(input: String): ParseResult[List[DiagramParseTree]] = {
    parseAll(diagrams, input)
  }

  private def diagrams: Parser[List[DiagramParseTree]] = rep(diagram)

  private def diagram: Parser[DiagramParseTree] = {
    diagramName ~ leftBrace ~ palettes ~ rightBrace ^^ { parseResult =>
      val diagramName ~ _ ~ palettes ~ _ = parseResult
      DiagramParseTree(diagramName, palettes)
    }
  }

  private def palettes: Parser[List[PaletteParseTree]] = rep(palette)

  private def palette: Parser[PaletteParseTree] = {
    paletteName ~ leftBrace ~ nodes ~ rightBrace ^^ { parseResult =>
      val paletteName ~ _ ~ nodes ~ _ = parseResult
      PaletteParseTree(paletteName, nodes)
    }
  }

  private def nodes: Parser[List[NodeParseTree]] = rep(node)

  private def node: Parser[NodeParseTree] = ident ^^  NodeParseTree

  private def diagramName: Parser[String] = literal("diagram") ~> ident

  private def paletteName: Parser[String] = literal("palette") ~> ident

}
