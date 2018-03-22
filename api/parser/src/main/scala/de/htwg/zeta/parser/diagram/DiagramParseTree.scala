package de.htwg.zeta.parser.diagram


case class DiagramParseTree(name: String, palettes: List[PaletteParseTree])
case class PaletteParseTree(name: String, nodes: List[NodeParseTree])
case class NodeParseTree(name: String)