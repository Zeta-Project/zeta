package de.htwg.zeta.parser

import scalaz.Failure
import scalaz.Success
import scalaz.Validation
import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.gdsl.GraphicalDsl
import de.htwg.zeta.common.models.project.gdsl.diagram.Diagram
import de.htwg.zeta.common.models.project.gdsl.shape.Shape
import de.htwg.zeta.common.models.project.gdsl.style.Style
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

  private def checkStyleParser(styleInput: String): Either[Failure[ErrorResult],List[Style]] = {
    StyleParser.parseStyles(styleInput) match {
      case Left(value) => Left(ErrorResult.styleFailure(value))
      case Right(value) => StyleParseTreeTransformer.transform(value) match {
        case Success(value) => Right(value)
        case Failure(value) => Left(ErrorResult.styleFailure(value))
      }
    }
  }

  private def checkShapeParser(shapeInput: String, styles: List[Style], concept: Concept): Either[Failure[ErrorResult],Shape] = {
    ShapeParser.parseShapes(shapeInput) match {
      case Left(value) => Left(ErrorResult.shapeFailure(value))
      case Right(value) => ShapeParseTreeTransformer.transform(value,styles, concept) match {
        case Success(value) => Right(value)
        case Failure(value) => Left(ErrorResult.shapeFailure(value))
      }
    }
  }

  private def checkDiagramParser(diagramInput: String, shape: Shape): Either[Failure[ErrorResult],List[Diagram]] = {
    DiagramParser.parseDiagrams(diagramInput) match {
      case Left(value) => Left(ErrorResult.diagramFailure(value))
      case Right(value) => DiagramParseTreeTransformer.transform(value,shape.nodes) match {
        case Success(value) => Right(value)
        case Failure(value) => Left(ErrorResult.diagramFailure(value))
      }
    }
  }

  def parse(concept: Concept, styleInput: String, shapeInput: String, diagramInput: String): Validation[ErrorResult, GraphicalDsl] = {
    val styles = checkStyleParser(styleInput) match {
      case Right(value) => value
      case Left(err) => return err
    }
    val shape = checkShapeParser(shapeInput, styles, concept) match {
      case Right(value) => value
      case Left(err) => return err
    }
    val diagrams = checkDiagramParser(diagramInput,shape) match {
      case Right(value) => value
      case Left(err) => return err
    }

    Success(GraphicalDsl(
      id = (diagrams.hashCode() + styles.hashCode() + shape.hashCode()).toString,
      diagrams = diagrams,
      styles = styles,
      shape = shape
    ))
  }
}
