package generator.model.diagram

import generator.model.diagram.action.ActionGroup
import generator.model.diagram.edge.Edge
import generator.model.diagram.node.Node
import generator.model.style.HasStyle
import generator.model.style.Style
import generator.parser.Cache
import models.document.MetaModelEntity

/**
 * Created by julian on 24.11.15.
 * representation of a diagram
 */
sealed class Diagram private (
    val name: String,
    val globalActionGroups: Map[String, ActionGroup],
    val nodes: List[Node],
    val edges: List[Edge],
    override val style: Option[Style],
    val metamodel: MetaModelEntity)
  extends HasStyle

object Diagram {
  def apply(
    name: String,
    globActGrps: Map[String, ActionGroup],
    nodes: List[Node],
    edges: List[Edge],
    style: Option[Style],
    metamodel: MetaModelEntity,
    cache: Cache) = {

    new Diagram(name, globActGrps, nodes, edges, style, metamodel)
  }
}

