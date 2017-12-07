package de.htwg.zeta.server.generator.model.diagram

import de.htwg.zeta.server.generator.model.diagram.action.ActionGroup
import de.htwg.zeta.server.generator.model.diagram.edge.Edge
import de.htwg.zeta.server.generator.model.diagram.node.Node
import de.htwg.zeta.server.generator.model.style.HasStyle
import de.htwg.zeta.server.generator.model.style.Style
import de.htwg.zeta.server.generator.parser.Cache
import de.htwg.zeta.common.models.entity.GraphicalDsl

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
    val metamodel: GraphicalDsl)
  extends HasStyle

object Diagram {
  def apply(
    name: String,
    globActGrps: Map[String, ActionGroup],
    nodes: List[Node],
    edges: List[Edge],
    style: Option[Style],
    metamodel: GraphicalDsl,
    cache: Cache): Diagram = {

    new Diagram(name, globActGrps, nodes, edges, style, metamodel)
  }
}

