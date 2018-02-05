package de.htwg.zeta.parser.diagram


case class PaletteParseTree(name: String, nodes: Seq[String])
case class DiagramParseTree(name: String, palettes: Seq[PaletteParseTree])