package models.model.instance

import models.metaModel.MetaModel
import models.metaModel.mCore._
import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.collection.immutable._

// TODO: this is WIP

object ModelReads {

  sealed trait Link
  case class ToEdges(`type`: MReference, edges: Seq[Edge]) extends Link
  case class ToNodes(`type`: MClass, nodes: Seq[Node]) extends Link

  def emtpyNode(id: String) = Node(id, MClass("",false,Seq[MClass](), Seq[MLinkDef](),Seq[MLinkDef](),Seq[MAttribute]()),Seq[Edge](),Seq[Edge](), Seq[Attribute]())
  def emtpyEdge(id: String) = new Edge(id, MReference("", false, false,Seq[MLinkDef](),Seq[MLinkDef](),Seq[MAttribute]()), Seq[Node](), Seq[Node](),Seq[Attribute]())

  private trait InvalidLink {
    val message: String
  }

  def metaModelDefinitionReads(implicit meta: MetaModel): Reads[ModelData] = {
    val mapReads = elementMapReads(meta)
    ((__ \ "name").read[String] and
      (__ \ "elements").read[Map[String, ModelElement]](mapReads)
      ) (ModelData.apply _)
  }

  def elementMapReads(implicit meta: MetaModel) = new Reads[Map[String, ModelElement]] {
    implicit val elementReads = modelElementReads(meta)
    override def reads(json: JsValue): JsResult[Map[String, ModelElement]] = {
      json.validate[Seq[ModelElement]] match {
        case JsSuccess(elements, _) => JsSuccess(elements.map(e => e.id -> e).toMap)
        case JsError(e) => JsError(e)
      }
    }
  }


  def modelElementReads(implicit meta: MetaModel) = new Reads[ModelElement] {

    def hasKey(name: String, json: JsValue) = (json \ name).toOption.isDefined

    override def reads(json: JsValue): JsResult[ModelElement] = {
      if (hasKey("mClass", json)) {
        json.validate(nodeReads.map(_.asInstanceOf[ModelElement]))
      } else if (hasKey("mReference", json)) {
        json.validate(edgeReads.map(_.asInstanceOf[ModelElement]))
      } else {
        JsError("Unknown type (neither a node nor an edge)")
      }
    }
  }

  val unknownMClassError = ValidationError("Unknown mClass")
  val unknownMReferenceError = ValidationError("Unknown mReference")

  def nodeReads(implicit meta: MetaModel): Reads[Node] = (
    (__ \ "id").read[String] and
      (__ \ "mClass").read[String].filter(unknownMClassError) {
        s => meta.concept.elements.get(s).exists(mObj => mObj.isInstanceOf[MClass])
      }.map {
        s => meta.concept.elements.get(s).get.asInstanceOf[MClass]
      } and
      (__ \ "outputs").read(Seq[Edge]()) and
      (__ \ "inputs").read(Seq[Edge]()) and
      (__ \ "attributes").read(Seq[Attribute]())
    ) (Node.apply2 _)

  def edgeReads(implicit meta: MetaModel): Reads[Edge] = (
    (__ \ "id").read[String] and
      (__ \ "mReference").read[String].filter(unknownMReferenceError) {
        s => meta.concept.elements.get(s).exists(mObj => mObj.isInstanceOf[MReference])
      }.map {
        s => meta.concept.elements.get(s).get.asInstanceOf[MReference]
      } and
      (__ \ "source").read(Seq[Node]()) and
      (__ \ "target").read(Seq[Node]()) and
      (__ \ "attributes").read(Seq[Attribute]())
    ) (Edge.apply2 _)


}
