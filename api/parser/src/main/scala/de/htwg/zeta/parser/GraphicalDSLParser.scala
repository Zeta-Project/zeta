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

  object ErrorResult {
    def styleFailure(error: String): Failure[ErrorResult] = Failure(ErrorResult("style", error))
    def shapeFailure(error: String): Failure[ErrorResult] = Failure(ErrorResult("shape", error))
    def diagramFailure(error: String): Failure[ErrorResult] = Failure(ErrorResult("diagram", error))
  }
  case class ErrorResult(errorDsl: String, error: String)

  def parse(concept: Concept, styleInput: String, shapeInput: String, diagramInput: String): Validation[ErrorResult, GraphicalDsl] = {
    val styleParseTree = StyleParser.parseStyles(styleInput)
    if (!styleParseTree.successful) {
      return ErrorResult.styleFailure(styleParseTree.toString)
    }
    val styles = StyleParseTreeTransformer.transform(styleParseTree.getOrElse(List()))
    if (!styles.isSuccess) {
      return ErrorResult.styleFailure(styles.toEither.left.toString)
    }
    val shapeParseTree = ShapeParser.parseShapes(shapeInput)
    if (!shapeParseTree.successful) {
      return ErrorResult.shapeFailure(shapeParseTree.toString)
    }
    val shape = ShapeParseTreeTransformer.transform(shapeParseTree.getOrElse(List()), styles.getOrElse(List()), concept)
    if (!shape.isSuccess) {
      return ErrorResult.shapeFailure(shape.toEither.left.toString)
    }
    val diagramParseTree = DiagramParser.parseDiagrams(diagramInput)
    if (!diagramParseTree.successful) {
      return ErrorResult.diagramFailure(diagramParseTree.toString)
    }
    val diagrams = DiagramParseTreeTransformer.transform(diagramParseTree.getOrElse(List()), shape.getOrElse(Shape(List(), List())).nodes)
    if (!diagrams.isSuccess) {
      return ErrorResult.diagramFailure(diagrams.toEither.left.toString)
    }
    Success(GraphicalDsl(
      id = (diagrams.hashCode() + styles.hashCode() + shape.hashCode()).toString,
      diagrams = diagrams.getOrElse(List()),
      styles = styles.getOrElse(List()),
      shape = shape.getOrElse(Shape(List(), List()))
    ))

  }

}
