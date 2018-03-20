package de.htwg.zeta.parser

import scalaz.Failure
import scalaz.Success
import scalaz.Validation

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.gdsl.GraphicalDsl
import de.htwg.zeta.common.models.project.gdsl.shape.Shape
import de.htwg.zeta.parser.diagram.DiagramParseTreeTransformer
import de.htwg.zeta.parser.diagram.DiagramParser
import de.htwg.zeta.parser.shape.ShapeParseTreeTransformer
import de.htwg.zeta.parser.shape.parser.ShapeParser
import de.htwg.zeta.parser.style.StyleParseTreeTransformer
import de.htwg.zeta.parser.style.StyleParser
import de.htwg.zeta.parser.style.StyleParserImpl

class GraphicalDSLParser {
  val styleParser: StyleParser = new StyleParserImpl

  def parse(concept: Concept, styleInput: String, shapeInput: String, diagramInput: String): Validation[List[String], GraphicalDsl] = {
    val styleParseTree = styleParser.parseStyles(styleInput)
    val styles = StyleParseTreeTransformer.transform(styleParseTree.getOrElse(List()))
    if (!styles.isSuccess) {
      Failure(styles.toEither.left.getOrElse(List()))
    }
    val shapeParseTree = ShapeParser.parseShapes(shapeInput)
    val shape = ShapeParseTreeTransformer.transform(shapeParseTree.getOrElse(List()), styles.getOrElse(List()), concept)
    if (!shape.isSuccess) {
      Failure(shape.toEither.left.getOrElse(List()))
    }
    val diagramParseTree = DiagramParser.parseDiagrams(diagramInput)
    val diagrams = DiagramParseTreeTransformer.transform(diagramParseTree.getOrElse(List()), shape.getOrElse(Shape(List(), List())).nodes)
    if (!diagrams.isSuccess) {
      Failure(diagrams.toEither.left.getOrElse(List()))
    }
    Success(GraphicalDsl(
      id = (diagrams.hashCode() + styles.hashCode() + shape.hashCode()).toString,
      diagrams = diagrams.getOrElse(List()),
      styles = styles.getOrElse(List()),
      shape = shape.getOrElse(Shape(List(), List()))
    ))

  }

}
