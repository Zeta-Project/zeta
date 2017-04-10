package models.modelDefinitions.model.elements

import models.modelDefinitions.metaModel.elements.{ AttributeValue, MClass, MReference }

import scala.collection.immutable.Seq
import scala.reflect.ClassTag

/**
 * Immutable domain model for representing models
 * Node -> MClass type instances, Edge -> MReference type instances
 */

/**
 * the topmost trait
 */
sealed trait ModelElement {
  val id: String
}

/**
 * a mixin that offers the attributes field
 */
sealed trait HasAttributes {
  val attributes: Seq[Attribute]
}

/**
 * a type that glues nodes and edges together
 */
sealed trait Link

/**
 * represents outgoing edges of a node
 * @param `type` the MReference instance that represents the type of the references
 * @param edges the linked edges
 */
case class ToEdges(`type`: MReference, edges: Seq[Edge]) extends Link

/**
 * represents nodes in reach of an edge
 * @param `type` the MClass instance that represents the type of the nodes
 * @param nodes
 */
case class ToNodes(`type`: MClass, nodes: Seq[Node]) extends Link

/**
 * Represents an MClass type instance
 * @param id the id of the node
 * @param `type` the MClass instance that represents the node's type
 * @param _outputs the outgoing edges
 * @param _inputs the incoming edges
 * @param attributes the attributes of the node
 */
class Node(
    val id: String,
    val `type`: MClass,
    _outputs: => Seq[ToEdges],
    _inputs: => Seq[ToEdges],
    val attributes: Seq[Attribute]
) extends ModelElement with HasAttributes {
  lazy val outputs = _outputs
  lazy val inputs = _inputs

  override def toString = {
    s"Node($id, ${`type`.name}, $outputs, $inputs, $attributes)"
  }

  // convenience method for updating links
  def updateLinks(_outputs: => Seq[ToEdges], _inputs: => Seq[ToEdges]) =
    new Node(id, `type`, _outputs, _inputs, attributes)

  // getter for attributes
  def getAttribute[T: ClassTag](name: String): T = {
    "AttributeValue".asInstanceOf[T]
  }

}

object Node {

  def apply(
    id: String,
    `type`: MClass,
    _outputs: => Seq[ToEdges],
    _inputs: => Seq[ToEdges],
    attributes: Seq[Attribute]
  ) = new Node(id, `type`, _outputs, _inputs, attributes)

  def apply2(
    id: String,
    `type`: MClass,
    outputs: Seq[ToEdges],
    inputs: Seq[ToEdges],
    attributes: Seq[Attribute]
  ) = new Node(id, `type`, outputs, inputs, attributes)

}

/**
 * Represents an MReference type instance
 * @param id the id of the edge
 * @param `type` the MReference instance that represents the edge's type
 * @param _source the nodes that are the origin of relationships
 * @param _target the nodes that can be reached
 * @param attributes
 */
class Edge(
    val id: String,
    val `type`: MReference,
    _source: => Seq[ToNodes],
    _target: => Seq[ToNodes],
    val attributes: Seq[Attribute])
  extends ModelElement with HasAttributes {

  lazy val source = _source
  lazy val target = _target

  override def toString = {
    s"Node($id, ${`type`.name}, $source, $target, $attributes)"
  }

  // convenience method for updating links
  def updateLinks(_source: => Seq[ToNodes], _target: => Seq[ToNodes]) =
    new Edge(id, `type`, _source, _target, attributes)
}

object Edge {
  def apply2(
    id: String,
    `type`: MReference,
    source: Seq[ToNodes],
    target: Seq[ToNodes],
    attributes: Seq[Attribute]
  ) = new Edge(id, `type`, source, target, attributes)
}

case class Attribute(
    val name: String,
    val value: Seq[AttributeValue])

