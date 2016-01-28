package generator.model.shapecontainer.connection

import generator.model.shapecontainer.shape.Shape
import generator.model.shapecontainer.shape.geometrics.{Text, GeometricModel}
import generator.model.style.Style
import generator.util.{PlacingSketch, CommonParserMethodes}

/**
 * Created by julian on 20.10.15.
 * represents a PlacingDefinition
 */
case class Placing(position_offset:Double,
                   position_distance:Option[Int]=None,
                   shapeCon:GeometricModel,
                   shape:Shape){
  /*TODO placing hat laut der grammatik eigentlich keine direkte beziehung zu einer Shape lediglich eine geomtric form, welche ein CDText sein kann ???*/
  def text = shapeCon match {
    case t:Text => Some(t)
    case _ => None
  }
}

object Placing extends CommonParserMethodes{
  def apply(attributes:PlacingSketch, parentStyle:Option[Style], ancestorShape:Shape) = parse(attributes, parentStyle, ancestorShape)
  def parse(attributes:PlacingSketch, parentStyle:Option[Style], ancestorShape:Shape):Placing = {
    /*mapping*/
    val tup = parse(placingPosition, attributes.position).get

    new Placing(tup._1, tup._2, attributes.shape.parse(None, parentStyle).get, ancestorShape)
  }

  def placingPosition:Parser[(Double, Option[Int])] = ("\\(\\s*offset\\s*=".r ~> argument_double) ~ (((",\\s*distance\\s*=".r ~> argument_int)?) <~ ")") ^^ {
    case offset ~ distance => (offset, distance)
  }

}
