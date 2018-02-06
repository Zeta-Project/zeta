package de.htwg.zeta.parser.diagram


case class DiagramParseTree(name: String, palettes: Seq[PaletteParseTree])
case class PaletteParseTree(name: String, nodes: Seq[NodeParseTree])
case class NodeParseTree(name: String)