package de.htwg.zeta.parser

import scalaz.Failure
import scalaz.Success
import scalaz.Validation
import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.gdsl.GraphicalDsl
import de.htwg.zeta.common.models.project.gdsl.shape.Shape
import de.htwg.zeta.parser.common.ParseError
import de.htwg.zeta.parser.diagram.DiagramParseTreeTransformer
import de.htwg.zeta.parser.diagram.DiagramParser
import de.htwg.zeta.parser.shape.ShapeParseTreeTransformer
import de.htwg.zeta.parser.shape.parser.ShapeParser
import de.htwg.zeta.parser.style.StyleParseTreeTransformer
import de.htwg.zeta.parser.style.StyleParser

class GraphicalDSLParser {

  object ErrorResult {
    private val styleDSL = "style"
    private val shapeDSL = "shape"
    private val diagramDSL = "diagram"

    def styleFailure(errors: List[String]): Failure[ErrorResult] = Failure(ErrorResult(styleDSL, errors, None))
    def styleFailure(parseError: ParseError): Failure[ErrorResult] = Failure(ErrorResult(styleDSL, List(parseError.message), Some(parseError.position)))

    def shapeFailure(errors: List[String]): Failure[ErrorResult] = Failure(ErrorResult(shapeDSL, errors, None))
    def shapeFailure(parseError: ParseError): Failure[ErrorResult] = Failure(ErrorResult(shapeDSL, List(parseError.message), Some(parseError.position)))

    def diagramFailure(errors: List[String]): Failure[ErrorResult] = Failure(ErrorResult(diagramDSL, errors, None))
    def diagramFailure(parseError: ParseError): Failure[ErrorResult] = Failure(ErrorResult(diagramDSL, List(parseError.message), Some(parseError.position)))
  }
  case class ErrorResult(errorDsl: String, errors: List[String], position: Option[(Int, Int)])

  def parse(concept: Concept, styleInput: String, shapeInput: String, diagramInput: String): Validation[ErrorResult, GraphicalDsl] = {
    val styleParseTree = StyleParser.parseStyles(styleInput)
    if (!styleParseTree.isRight) {
      return ErrorResult.styleFailure(styleParseTree.left.get)
    }
    val styles = StyleParseTreeTransformer.transform(styleParseTree.getOrElse(List()))
    if (!styles.isSuccess) {
      return ErrorResult.styleFailure(styles.toEither.left.get)
    }
    val shapeParseTree = ShapeParser.parseShapes(shapeInput)
    if (!shapeParseTree.isRight) {
      return ErrorResult.shapeFailure(shapeParseTree.left.get)
    }
    val shape = ShapeParseTreeTransformer.transform(shapeParseTree.getOrElse(List()), styles.getOrElse(List()), concept)
    if (!shape.isSuccess) {
      return ErrorResult.shapeFailure(shape.toEither.left.get)
    }
    val diagramParseTree = DiagramParser.parseDiagrams(diagramInput)
    if (!diagramParseTree.isRight) {
      return ErrorResult.diagramFailure(diagramParseTree.left.get)
    }
    val diagrams = DiagramParseTreeTransformer.transform(diagramParseTree.getOrElse(List()), shape.getOrElse(Shape(List(), List())).nodes)
    if (!diagrams.isSuccess) {
      return ErrorResult.diagramFailure(diagrams.toEither.left.get)
    }
    Success(GraphicalDsl(
      id = (diagrams.hashCode() + styles.hashCode() + shape.hashCode()).toString,
      diagrams = diagrams.getOrElse(List()),
      styles = styles.getOrElse(List()),
      shape = shape.getOrElse(Shape(List(), List()))
    ))

  }

}
