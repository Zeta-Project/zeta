package generator.model.diagram.edge

import generator.model.style.Style
import generator.parser.{ConnectionSketch, Cache, PropsAndComps}
import parser._

/**
 * Created by julian on 11.12.15.
 * representation of diagramm connection
 */
class Connection(corporateStyle:Option[Style], propsAndComps: PropsAndComps, c:Cache){
  implicit val cache = c
  val referencedConnection:Option[generator.model.shapecontainer.connection.Connection] = {
    val connectionSketch:ConnectionSketch = propsAndComps.ref
    connectionSketch.toConnection(corporateStyle, cache)
  }
  val vars = propsAndComps.propertiesAndCompartments.getOrElse(List()).filter(i => i._1 == "var").map(_._2).map(i => i._1 -> new Object()).toMap /*TODO new Object (at second use) needs to be resolved to an attribute but is not possible at the moment*/
  val vals = propsAndComps.propertiesAndCompartments.getOrElse(List()).filter(i => i._1 == "val").map(_._2).map(i => i._1 -> new Object()).toMap /*TODO new Object (at second use) needs to be resolved to an attribute but is not possible at the moment*/
}
