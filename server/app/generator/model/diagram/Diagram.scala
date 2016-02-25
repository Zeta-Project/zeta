package generator.model.diagram

import generator.model.diagram.action.ActionGroup
import generator.model.diagram.edge.Edge
import generator.model.diagram.node.Node
import generator.model.style.{Style, HasStyle}
import generator.parser.Cache
import models.metaModel.mCore.MClass

/**
 * Created by julian on 24.11.15.
 * representation of a diagram
 */
sealed class Diagram private (val name:String,
               val globalActionGroups:Map[String, ActionGroup],
               val nodes:List[Node],
               val edges:List[Edge],
               override val style: Option[Style],
               val metamodelElement:MClass) extends HasStyle

object Diagram {
  def apply(name:String, globActGrps:Map[String, ActionGroup], nodes:List[Node], edges:List[Edge], style:Option[Style], modelType:MClass, cache:Cache) = {
    new Diagram(name, globActGrps, nodes, edges, style, modelType)
  }
}
