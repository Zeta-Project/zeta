package models.model.instance

import models.metaModel.mCore.ScalarValue.{MString, MInt, MDouble, MBool}
import play.api.libs.json._

import scala.collection.immutable._

object ModelWrites {

  implicit val mObjectWrites = new Writes[ModelElement] {
    def writes(o: ModelElement): JsValue = {
      o match {
        case n: Node => Json.toJson(n)(nodeWrites)
        case e: Edge => Json.toJson(e)(edgeWrites)
      }
    }
  }

  implicit val nodeWrites: Writes[Node] = new Writes[Node] {
    def writes(n: Node): JsValue = {
      Json.obj(
        "id" -> n.id,
        "mClass" -> n.`type`.name,
        "outputs" -> transformEdges(n.outputs),
        "inputs" -> transformEdges(n.inputs)
        //"attributes" -> n.attributes
      )
    }
  }

  implicit val edgeWrites: Writes[Edge] = new Writes[Edge] {
    def writes(e: Edge): JsValue = {
      Json.obj(
        "id" -> e.id,
        "mReference" -> e.`type`.name,
        "source" -> transformNodes(e.source),
        "target" -> transformNodes(e.target)
      )
    }
  }

  def transformEdges(edges: Seq[ToEdges]) = {
    val elems = edges.map(
      e => e.`type`.name -> Json.toJsFieldJsValueWrapper(e.edges.map(_.id)))
    Json.obj(elems: _*)
  }

  def transformNodes(nodes: Seq[ToNodes]) = {
    val elems = nodes.map(
      n => n.`type`.name -> Json.toJsFieldJsValueWrapper(n.nodes.map(_.id)))
    Json.obj(elems: _*)
  }

}
