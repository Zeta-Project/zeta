package de.htwg.zeta.common.models.project.instance.elements

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.elements.AttributeValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.HasAttributeValues
import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MAttribute.AttributeMap
import de.htwg.zeta.common.models.project.concept.elements.Method
import de.htwg.zeta.common.models.project.concept.elements.Method.MethodMap


/** Represents an MReference type instance.
 *
 * @param name            the name of this edge
 * @param referenceName   the name of the MReference instance that represents the edge's type
 * @param sourceNodeName  the name of the node that are the origin of relationships
 * @param targetNodeName  the name of the node that can be reached
 * @param attributeValues a map with attribute names and the assigned values
 */
case class EdgeInstance(
    name: String,
    referenceName: String,
    sourceNodeName: String,
    targetNodeName: String,
    attributes: Seq[MAttribute],
    attributeValues: Map[String, AttributeValue],
    methods: Seq[Method]
) extends HasAttributeValues with AttributeMap with MethodMap

object EdgeInstance {

  def empty(name: String, referenceName: String, source: String, target: String): EdgeInstance =
    EdgeInstance(name, referenceName, source, target, Seq.empty, Map.empty, Seq.empty)

  trait EdgeMap {

    val edges: Seq[EdgeInstance]

    /** Edges mapped to their own names. */
    final val edgeMap: Map[String, EdgeInstance] = Option(edges).fold(
      Map.empty[String, EdgeInstance]
    ) { edges =>
      edges.filter(Option(_).isDefined).map(edge => (edge.name, edge)).toMap
      }

  }

}
