package models.model.instance

import models.metaModel.mCore.{MReference, MClass, AttributeValue}

case class ModelData(name: String, data: Map[String, ModelElement])

trait ModelElement {
  val id: String
}

class Node(
  val id: String,
  val `type`: MClass,
  _outputs: => Seq[Edge],
  _inputs: => Seq[Edge],
  val attributes: Seq[Attribute]
) extends ModelElement {
  lazy val outputs = _outputs
  lazy val inputs = _inputs
}

object Node {

  def apply(
    id: String,
    `type`: MClass,
    _outputs: => Seq[Edge],
    _inputs: => Seq[Edge],
    attributes: Seq[Attribute]
  ) = new Node(id, `type`, _outputs, _inputs, attributes)

  def apply2(
    id: String,
    `type`: MClass,
    outputs: Seq[Edge],
    inputs: Seq[Edge],
    attributes: Seq[Attribute]
  ) = new Node(id, `type`, outputs, inputs, attributes)

}

class Edge(
  val id: String,
  val `type`: MReference,
  _source: => Seq[Node],
  _target: => Seq[Node],
  val attributes: Seq[Attribute]
) extends ModelElement {
  lazy val source = _source
  lazy val target = _target
}

case class Attribute(
  name: String,
  value: Seq[AttributeValue]
)



