package de.htwg.zeta.server.generator.model.shapecontainer.connection

import de.htwg.zeta.server.generator.model.shapecontainer.shape.Shape
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Text
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.GeometricModel
import de.htwg.zeta.server.generator.model.style.Style
import de.htwg.zeta.server.generator.parser.PlacingSketch
import de.htwg.zeta.server.generator.parser.CommonParserMethods

/**
 * Created by julian on 20.10.15.
 * represents a PlacingDefinition
 */
sealed class Placing private (
    val position_offset: Double,
    val position_distance: Option[Int] = None,
    val shapeCon: GeometricModel,
    val shape: Shape
) {
  def text = shapeCon match {
    case t: Text => Some(t)
    case _ => None
  }
}

object Placing extends CommonParserMethods {
  def apply(attributes: PlacingSketch, parentStyle: Option[Style], ancestorShape: Shape) = parse(attributes, parentStyle, ancestorShape)
  def parse(attributes: PlacingSketch, parentStyle: Option[Style], ancestorShape: Shape): Placing = {
    // mapping
    val tup = parse(placingPosition, attributes.position).get

    new Placing(tup._1, tup._2, attributes.shape.parse(None, parentStyle).get, ancestorShape)
  }

  def placingPosition: Parser[(Double, Option[Int])] = ("\\(\\s*offset\\s*=".r ~> argument_double) ~ (((",\\s*distance\\s*=".r ~> argument_int)?) <~ ")") ^^ {
    case offset ~ distance => (offset, distance)
  }

}
