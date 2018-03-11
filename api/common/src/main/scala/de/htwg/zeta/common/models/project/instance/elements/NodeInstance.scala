package de.htwg.zeta.common.models.project.instance.elements

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.elements.AttributeValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.HasAttributeValues
import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MAttribute.AttributeMap
import de.htwg.zeta.common.models.project.concept.elements.Method
import de.htwg.zeta.common.models.project.concept.elements.Method.MethodMap

/** Represents an MClass type instance.
 *
 * @param name            the name of this node
 * @param className       the name of the MClass instance that represents the node's type
 * @param outputEdgeNames the names of the outgoing edges
 * @param inputEdgeNames  the names of the incoming edges
 * @param attributeValues a map with attribute names and the assigned values
 */
case class NodeInstance(
    name: String,
    className: String,
    outputEdgeNames: Seq[String],
    inputEdgeNames: Seq[String],
    attributes: Seq[MAttribute],
    attributeValues: Map[String, AttributeValue],
    methods: Seq[Method]
) extends AttributeMap with HasAttributeValues with MethodMap

object NodeInstance {

  def empty(name: String, className: String, outputs: Seq[String], inputs: Seq[String]): NodeInstance =
    NodeInstance(name, className, outputs, inputs, Seq.empty, Map.empty, Seq.empty)

  trait NodeMap {

    val nodes: Seq[NodeInstance]

    /** Nodes mapped to their own names. */
    final val nodeMap: Map[String, NodeInstance] = Option(nodes).fold(
      Map.empty[String, NodeInstance]
    ) { nodes =>
      nodes.filter(Option(_).isDefined).map(node => (node.name, node)).toMap
      }

  }

}
