package generator.model.diagram.node

import generator.model.shapecontainer.shape.Compartment
import generator.model.shapecontainer.shape.geometrics.Text
import generator.model.style.Style
import generator.parser.{ShapeSketch, Cache}
import models.metaModel.mCore.{MReference, MClass, MAttribute}
import parser._

/**
 * Created by julian on 30.11.15.
 * diagrams shape definition
 */
class DiaShape(corporateStyle:Option[Style], shape:String,
               propertiesAndCompartments:Option[List[(String, (String, String))]], c:Cache, mc:MClass, mRmap:Map[String, MReference]){
  implicit val cache = c
  val referencedShape:generator.model.shapecontainer.shape.Shape = {
    val shapesketch:ShapeSketch = shape
    /*Hier werden aus ShapeSketches endlich eigentliche Shapes!*/
    shapesketch.toShape(corporateStyle)
  }
  var vals = Map[String, Text]()
  var vars = Map[MAttribute, Text]()
  var nests = Map[MReference, Compartment]()
    if(propertiesAndCompartments isDefined) {

      vars = propertiesAndCompartments.get.filter(i => i._1 == "var").map(_._2).map(i =>
        mc.attributes.filter(_.name == i._1).head -> referencedShape.textMap.get(i._2)).toMap

      vals = propertiesAndCompartments.get.filter(i => i._1 == "val").map(_._2).map(i => i._1 -> referencedShape.textMap.get(i._2)).toMap

      nests = propertiesAndCompartments.get.filter(i => i._1 == "nest").map(_._2).map(i =>
        mRmap(i._1) -> referencedShape.compartmentMap.get(i._2)).toMap
  }
}
