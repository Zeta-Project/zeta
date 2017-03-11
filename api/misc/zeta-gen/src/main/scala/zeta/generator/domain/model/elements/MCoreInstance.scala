package zeta.generator.domain.model.elements

import zeta.generator.domain.metaModel.elements.{AttributeValue, MReference, MClass}

import scala.collection.immutable._

trait ModelElement {
  val id: String
}

sealed trait Link

case class ToEdges(`type`: MReference, edges: Seq[Edge]) extends Link

case class ToNodes(`type`: MClass, nodes: Seq[Node]) extends Link

class Node(
  val id: String,
  val `type`: MClass,
  _outputs: => Seq[ToEdges],
  _inputs: => Seq[ToEdges],
  val attributes: Seq[Attribute]
) extends ModelElement {
  lazy val outputs = _outputs
  lazy val inputs = _inputs

  override def toString = {
    s"Node($id, ${`type`.name}, $outputs, $inputs, $attributes)"
  }

  def updateLinks(_outputs: => Seq[ToEdges], _inputs: => Seq[ToEdges]) =
    new Node(id, `type`, _outputs, _inputs, attributes)
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

class Edge(
  val id: String,
  val `type`: MReference,
  _source: => Seq[ToNodes],
  _target: => Seq[ToNodes],
  val attributes: Seq[Attribute]
) extends ModelElement {
  lazy val source = _source
  lazy val target = _target

  override def toString = {
    s"Node($id, ${`type`.name}, $source, $target, $attributes)"
  }

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
  name: String,
  value: Seq[AttributeValue]
)