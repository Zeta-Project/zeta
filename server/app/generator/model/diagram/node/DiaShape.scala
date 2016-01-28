package generator.model.diagram.node

import generator.model.shapecontainer.shape.Compartment
import generator.model.shapecontainer.shape.geometrics.Text
import generator.model.style.Style
import generator.parser.{ShapeSketch, Cache}
import parser._

/**
 * Created by julian on 30.11.15.
 * diagrams shape definition
 */
class DiaShape(corporateStyle:Option[Style], shape:String, propertiesAndCompartments:Option[List[(String, (String, String))]], c:Cache){
  implicit val cache = c
  val referencedShape:generator.model.shapecontainer.shape.Shape = {
    val shapesketch:ShapeSketch = shape
    /*Hier werden aus ShapeSketches endlich eigentliche Shapes!*/
    shape.toShape(corporateStyle)
  }
  var vars = Map[String, Text]()
  var vals = Map[String, Text]()
  var nests = Map[String, Compartment]()
    if(propertiesAndCompartments isDefined) {
      vars = propertiesAndCompartments.get.filter(i => i._1 == "var").map(_._2).map(i => i._1 -> referencedShape.textMap.get(i._2)).toMap/*TODO i._1 (at second use) needs to be resolved to an attribute but is not possible at the moment*/
      vals = propertiesAndCompartments.get.filter(i => i._1 == "val").map(_._2).map(i => i._1 -> referencedShape.textMap.get(i._2)).toMap
      nests = propertiesAndCompartments.get.filter(i => i._1 == "nest").map(_._2).map(i => i._1 -> referencedShape.compartmentMap.get(i._2)).toMap
  }
}
