package generator.model.shapecontainer.connection

import generator.model.shapecontainer.shape.Shape
import generator.model.shapecontainer.shape.geometrics.{GeometricModel, Text}
import generator.model.style.Style
import generator.parser.{CommonParserMethods, PlacingSketch}

/**
 * Created by julian on 20.10.15.
 * represents a PlacingDefinition
 * Extended for VrGeneratorConnectionDefinition on 27.10.16
 */
sealed class Placing private (val position_offset:Double,
                   val position_distance:Option[Int]=None,
                   val shapeCon:GeometricModel,
                   val shape:Shape,
                   val attributes: Attributes){
  def text = shapeCon match {
    case t:Text => Some(t)
    case _ => None
  }
}

object Placing extends CommonParserMethods{
  def apply(attributes:PlacingSketch, parentStyle:Option[Style], ancestorShape:Shape) = parse(attributes, parentStyle, ancestorShape)
  def parse(attributes:PlacingSketch, parentStyle:Option[Style], ancestorShape:Shape):Placing = {
    /*mapping*/
    val tup:(Double,Option[Int]) = parse(placingPosition, attributes.position).get
    val attributesParam = Attributes.apply(attributes)

    // adjusted to match new constructor definition
    new Placing(tup._1, tup._2, attributes.shape.parse(None, parentStyle).get, ancestorShape, attributesParam)
  }

  def placingPosition:Parser[(Double, Option[Int])] = ("\\(\\s*offset\\s*=".r ~> argument_double) ~ (((",\\s*distance\\s*=".r ~> argument_int)?) <~ ")") ^^ {
    case offset ~ distance => (offset, distance)
  }
}

// Created for VrGeneratorConnectionDefinition on 27.10.16
sealed class Attributes private (val typ: String, val points: List[(Int, Int)])

// Created for VrGeneratorConnectionDefinition on 27.10.16
object Attributes extends CommonParserMethods{
  def apply(attributes: PlacingSketch) = {
    val points: List[(Int,Int)] = attributes.shape.attributes.map(parse(placingPoint, _).get)
    new Attributes(attributes.shape.typ, points)
  }

  def placingPoint:Parser[(Int, Int)] = ("point\\(\\s*x\\s*=".r ~> argument_int) ~ ((",\\s*y\\s*=".r ~> argument_int) <~ ")") ^^ {
    case x ~ y => (x, y)
  }
}


