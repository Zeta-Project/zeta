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

class GraphicalDSLParser {

  def parse(concept: Concept, styleInput: String, shapeInput: String, diagramInput: String): Validation[List[String], GraphicalDsl] = {
    val styleParseTree = StyleParser.parseStyles(styleInput)
    if (!styleParseTree.successful) {
      return Failure(List("Failed to parse style: " + styleParseTree.toString))
    }
    val styles = StyleParseTreeTransformer.transform(styleParseTree.getOrElse(List()))
    if (!styles.isSuccess) {
      return Failure(styles.toEither.left.getOrElse(List()))
    }
    val shapeParseTree = ShapeParser.parseShapes(shapeInput)
    if (!shapeParseTree.successful) {
      return Failure(List("Failed to parse shape: " + shapeParseTree.toString))
    }
    val shape = ShapeParseTreeTransformer.transform(shapeParseTree.getOrElse(List()), styles.getOrElse(List()), concept)
    if (!shape.isSuccess) {
      return Failure(shape.toEither.left.getOrElse(List()))
    }
    val diagramParseTree = DiagramParser.parseDiagrams(diagramInput)
    if (!diagramParseTree.successful) {
      return Failure(List("Failed to parse diagram: " + diagramParseTree.toString))
    }
    val diagrams = DiagramParseTreeTransformer.transform(diagramParseTree.getOrElse(List()), shape.getOrElse(Shape(List(), List())).nodes)
    if (!diagrams.isSuccess) {
      return Failure(diagrams.toEither.left.getOrElse(List()))
    }
    Success(GraphicalDsl(
      id = (diagrams.hashCode() + styles.hashCode() + shape.hashCode()).toString,
      diagrams = diagrams.getOrElse(List()),
      styles = styles.getOrElse(List()),
      shape = shape.getOrElse(Shape(List(), List()))
    ))

  }

}
