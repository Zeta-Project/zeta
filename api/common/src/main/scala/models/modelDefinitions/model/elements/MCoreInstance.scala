package models.modelDefinitions.model.elements

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.AttributeValue
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MReference

/**
 * Immutable domain model for representing models
 * Node -> MClass type instances, Edge -> MReference type instances
 */

/**
 * the topmost trait
 */
sealed trait ModelElement {
  val name: String
}

/**
 * a mixin that offers the attributes field
 */
sealed trait HasAttributes {
  val attributes: Map[String, Seq[AttributeValue]]
}

/**
 * a type that glues nodes and edges together
 */
sealed trait Link

/**
 * represents outgoing edges of a node
 *
 * @param reference the MReference instance that represents the type of the references
 * @param edgeNames the names of the linked edges
 */
case class ToEdges(reference: MReference, edgeNames: Seq[String]) extends Link

/**
 * represents nodes in reach of an edge
 *
 * @param clazz     the MClass instance that represents the type of the nodes
 * @param nodeNames the names of the nodes
 */
case class ToNodes(clazz: MClass, nodeNames: Seq[String]) extends Link

/**
 * Represents an MClass type instance
 *
 * @param name       the name of the node
 * @param clazz      the MClass instance that represents the node's type
 * @param outputs    the outgoing edges
 * @param inputs     the incoming edges
 * @param attributes a map with attribute names and the assigned values
 */
case class Node(
    name: String,
    clazz: MClass,
    outputs: Seq[ToEdges],
    inputs: Seq[ToEdges],
    attributes: Map[String, Seq[AttributeValue]]
) extends ModelElement with HasAttributes


/**
 * Represents an MReference type instance
 *
 * @param name       the name of the edge
 * @param reference  the MReference instance that represents the edge's type
 * @param source     the nodes that are the origin of relationships
 * @param target     the nodes that can be reached
 * @param attributes a map with attribute names and the assigned values
 */
case class Edge(
    name: String,
    reference: MReference,
    source: Seq[ToNodes],
    target: Seq[ToNodes],
    attributes: Map[String, Seq[AttributeValue]]
) extends ModelElement with HasAttributes
