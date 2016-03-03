package models.model.instance

import models.metaModel.MetaModel
import models.metaModel.mCore._
import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.collection.immutable._

// TODO: this is WIP

object ModelReads {

  def emtpyNode(id: String) = Node(id, MClass("", false, Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]()), Seq[ToEdges](), Seq[ToEdges](), Seq[Attribute]())

  def emtpyEdge(id: String) = new Edge(id, MReference("", false, false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]()), Seq[ToNodes](), Seq[ToNodes](), Seq[Attribute]())

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
        case JsSuccess(elements, _) => {
          val map = elements.map(e => e.id -> e).toMap
          if(map.size == elements.size) JsSuccess(map) else JsError("elements must have unique names")
        }
        case JsError(e) => JsError(e)
      }
    }

    def finalize(map: Map[String, ModelElement]): JsResult[Map[String, ModelElement]] = {
      JsSuccess(wire(map))
    }

    def wireNodes(newMap: => Map[String, ModelElement], old: Seq[ToNodes]): Seq[ToNodes] = {
      old.map(m => m.copy(nodes = m.nodes.map(n => newMap(n.id).asInstanceOf[Node])))
    }

    def wireEdges(newMap: => Map[String, ModelElement], old: Seq[ToEdges]): Seq[ToEdges] = {
      old.map(m => m.copy(edges = m.edges.map(e => newMap(e.id).asInstanceOf[Edge])))
    }

    def wire(mapping: Map[String, ModelElement]): Map[String, ModelElement] = {
      val builder = new {
        val finalMap: Map[String, ModelElement] = mapping.mapValues {
          _ match {
            case n: Node => n.updateLinks(
              wireEdges(finalMap, n.inputs),
              wireEdges(finalMap, n.outputs)
            )
            case e: Edge => e.updateLinks(
              wireNodes(finalMap, e.source),
              wireNodes(finalMap, e.target)
            )
          }
        }
      }
      builder.finalMap
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

  val invalidToEdgesError = ValidationError("edge reference has invalid type")
  val invalidToNodesError = ValidationError("node reference has invalid type")

  val invalidToEdgesError2 = ValidationError("edge reference has invalid type 2")
  val invalidToNodesError2 = ValidationError("node reference has invalid type 2")

  def attributeSeqReads(implicit meta: MetaModel) = new Reads[Seq[Attribute]] {
    def reads(json: JsValue) = {
      JsSuccess(Seq[Attribute]())
    }
  }

  private def extractEdges(m: Map[String, Seq[String]])(implicit meta: MetaModel): Seq[ToEdges] = {
    m.map { case (k, v) =>
      val t = meta.concept.elements(k).asInstanceOf[MReference]
      val e = v.map(emtpyEdge)
      ToEdges(t, e)
    }
  }.toList

  def nodeReads(implicit meta: MetaModel): Reads[Node] = (
    (__ \ "id").read[String] and
      (__ \ "mClass").read[String].filter(unknownMClassError) {
        s => meta.concept.containsMClass(s)
      }.map {
        s => meta.concept.getMClass(s).get
      } and
      (__ \ "outputs").read[Map[String, Seq[String]]].filter(invalidToEdgesError) {
        e => e.keys.forall(s => meta.concept.containsMReference(s))
      }.map(extractEdges) and
      (__ \ "inputs").read[Map[String, Seq[String]]].filter(invalidToEdgesError) {
        e => e.keys.forall(s => meta.concept.containsMReference(s))
      }.map(extractEdges) and
      (__ \ "attributes").read(Seq[Attribute]())
    ) (Node.apply2 _).filter(invalidToEdgesError2) { n =>
    n.inputs.forall(e => n.`type`.typeHasInput(e.`type`.name)) &&
      n.outputs.forall(e => n.`type`.typeHasOutput(e.`type`.name))
  }

  private def extractNodes(m: Map[String, Seq[String]])(implicit meta: MetaModel): Seq[ToNodes] = {
    m.map { case (k, v) =>
      val t = meta.concept.elements(k).asInstanceOf[MClass]
      val n = v.map(emtpyNode)
      ToNodes(t, n)
    }
  }.toList

  def edgeReads(implicit meta: MetaModel): Reads[Edge] = (
    (__ \ "id").read[String] and
      (__ \ "mReference").read[String].filter(unknownMReferenceError) {
        s => meta.concept.containsMReference(s)
      }.map {
        s => meta.concept.getMReference(s).get
      } and
      (__ \ "source").read[Map[String, Seq[String]]].filter(invalidToNodesError) {
        n => n.keys.forall(s => meta.concept.containsMClass(s))
      }.map(extractNodes) and
      (__ \ "target").read[Map[String, Seq[String]]].filter(invalidToNodesError) {
        n => n.keys.forall(s => meta.concept.containsMClass(s))
      }.map(extractNodes) and
      (__ \ "attributes").read(Seq[Attribute]())
    ) (Edge.apply2 _).filter(invalidToNodesError2) { n =>
    n.source.forall(e => n.`type`.source.exists(l => e.`type`.typeHasSuperType(l.mType.name))) &&
      n.target.forall(e => n.`type`.target.exists(l => e.`type`.typeHasSuperType(l.mType.name)))

  }


}
