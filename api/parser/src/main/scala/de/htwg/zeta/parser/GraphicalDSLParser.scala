package de.htwg.zeta.parser

import scalaz.Failure
import scalaz.Success
import scalaz.Validation

import de.htwg.zeta.common.model.MetaModel
import de.htwg.zeta.common.model.shape.Shape
import de.htwg.zeta.common.models.modelDefinitions.concept.Concept
import de.htwg.zeta.parser.diagram.DiagramParseTreeTransformer
import de.htwg.zeta.parser.diagram.DiagramParser
import de.htwg.zeta.parser.shape.ShapeParseTreeTransformer
import de.htwg.zeta.parser.shape.parser.ShapeParser
import de.htwg.zeta.parser.style.StyleParseTreeTransformer
import de.htwg.zeta.parser.style.StyleParser
import de.htwg.zeta.parser.style.StyleParserImpl

object GraphicalDSLParser {
  val styleParser: StyleParser = new StyleParserImpl

  def parse(concept: Concept, styleInput: String, shapeInput: String, diagramInput: String): Validation[List[String], MetaModel] = {
    val styleParseTree = styleParser.parseStyles(styleInput).getOrElse(List())
    val styles = StyleParseTreeTransformer.transform(styleParseTree)
    if (styles.isSuccess) {
      val shapeParseTree = ShapeParser.parseShapes(shapeInput).getOrElse(List())
      val shape = ShapeParseTreeTransformer.transform(shapeParseTree, styles.getOrElse(List()), concept)
      if (shape.isSuccess) {
        val diagramParseTree = DiagramParser.parseDiagrams(diagramInput).get
        val diagrams = DiagramParseTreeTransformer.transform(diagramParseTree, shape.getOrElse(Shape(List(), List())).nodes)
        if (diagrams.isSuccess) {
          Success(MetaModel(
            id = (diagrams.hashCode() + styles.hashCode() + shape.hashCode()).toString,
            diagrams = diagrams.getOrElse(List()),
            styles = styles.getOrElse(List()),
            shape = shape.getOrElse(Shape(List(), List()))
          ))
        } else {
          Failure(diagrams.toEither.left.getOrElse(List()))
        }
      } else {
        Failure(shape.toEither.left.getOrElse(List()))
      }
    } else {
      Failure(styles.toEither.left.getOrElse(List()))
    }
  }

}
