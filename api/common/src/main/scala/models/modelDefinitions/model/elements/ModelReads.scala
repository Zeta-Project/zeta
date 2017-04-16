package models.modelDefinitions.model.elements

import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements._
import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.annotation.tailrec
import scala.collection.immutable._

/**
 * Reads[T] for Model structures (bottom of file)
 * Contains also the necessary logic for graph initialization
 */

object ModelReads {

  def emtpyNode(id: String) = {
    val mclass = MClass("", false, Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    Node(id, mclass, Seq[ToEdges](), Seq[ToEdges](), Seq[Attribute]())
  }

  def emtpyEdge(id: String) = {
    val mReference = MReference("", false, false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    new Edge(id, mReference, Seq[ToNodes](), Seq[ToNodes](), Seq[Attribute]())
  }

  private trait InvalidLink {
    val message: String
  }

  def elementMapReads(implicit meta: MetaModel) = new Reads[Map[String, ModelElement]] {
    implicit val elementReads = modelElementReads(meta)

    override def reads(json: JsValue): JsResult[Map[String, ModelElement]] = {
      json.validate[Seq[ModelElement]] match {
        case JsSuccess(elements, _) => {
          val map = elements.map(e => e.id -> e).toMap
          if (map.size == elements.size) finalize(map) else JsError("elements must have unique names")
        }
        case JsError(e) => JsError(e)
      }
    }

    def finalize(map: Map[String, ModelElement]): JsResult[Map[String, ModelElement]] = {
      val refErrors = validateRefs(map)
      if (refErrors.isEmpty) {
        JsSuccess(wire(map))
      } else {
        JsError(s"Model contains invalid references: ${refErrors.mkString(", ")}")
      }
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
              wireEdges(finalMap, n.outputs),
              wireEdges(finalMap, n.inputs)
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

  private def validateRefs(mapping: Map[String, ModelElement]): List[String] = {
    val values = mapping.values.toList

    def checkEdgeLinks(source: String, links: Seq[ToEdges]): List[String] = {
      for {
        toEdges <- links.toList;
        edge <- toEdges.edges;
        target = edge.id if !mapping.contains(target) || !mapping(target).isInstanceOf[Edge]
      } yield {
        s"invalid link to edge: '$source' -> '$target' ('$target' is missing or doesn't match expected type)"
      }
    }

    def checkNodeLinks(source: String, links: Seq[ToNodes]): List[String] = {
      for {
        toNodes <- links.toList;
        node <- toNodes.nodes;
        target = node.id if !mapping.contains(target) || !mapping(target).isInstanceOf[Node]
      } yield {
        s"invalid link to node: '$source' -> '$target' ('$target' is missing or doesn't match expected type)"
      }
    }

    def processEdge(e: Edge): List[String] = {
      checkNodeLinks(e.id, e.source) :::
        checkNodeLinks(e.id, e.target)
    }

    def processNode(n: Node): List[String] = {
      checkEdgeLinks(n.id, n.inputs) :::
        checkEdgeLinks(n.id, n.outputs)
    }

    def processElement(mObj: ModelElement): List[String] = mObj match {
      case n: Node => processNode(n)
      case e: Edge => processEdge(e)
      case _ => Nil
    }

    @tailrec
    def walk(remaining: List[ModelElement], accErrors: List[String]): List[String] = {
      remaining match {
        case Nil => accErrors
        case mObj :: tail => walk(tail, accErrors ::: processElement(mObj))
      }
    }

    walk(values, Nil)
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

  val attributeDuplicateError = ValidationError("object may contain the same attribute only once")

  private def extractEdges(m: Map[String, Seq[String]])(implicit meta: MetaModel): Seq[ToEdges] = {
    m.map {
      case (k, v) =>
        val t = meta.elements(k).asInstanceOf[MReference]
        val e = v.map(emtpyEdge)
        ToEdges(t, e)
    }
  }.toList

  def attributesReads(mAttributes: Seq[MAttribute]) = Reads { json =>

    def locate(e: scala.Seq[(JsPath, scala.Seq[ValidationError])], idx: Int) = e.map { case (p, valerr) => (JsPath(idx)) ++ p -> valerr }

    buildAttributeMap(mAttributes, json)
      .iterator.zipWithIndex.foldLeft(Right(Vector.empty): Either[scala.Seq[(JsPath, scala.Seq[ValidationError])], Vector[Attribute]]) {
      case (Right(vs), (JsSuccess(v, _), _)) => Right(vs :+ v)
      case (Right(_), (JsError(e), idx)) => Left(locate(e, idx))
      case (Left(e), (_: JsSuccess[_], _)) => Left(e)
      case (Left(e1), (JsError(e2), idx)) => Left(e1 ++ locate(e2, idx))
    }.fold(JsError.apply, { res =>
      JsSuccess(res.toList)
    })
  }.filter(ValidationError("Attribute bounds check failed")) { attributes =>
    attributes.forall { a =>
      mAttributes.exists { ma =>
        ma.name == a.name && checkAttributeMult(a, ma)
      }
    }
  }

  private def buildAttributeMap(mAttributes: Seq[MAttribute], json: JsValue) = {
    mAttributes.map(a => a.name -> a.`type`).toMap.map {
      case (key, ScalarType.Double) => (__ \ key).read[List[Double]].map(ds => Attribute(key, ds.map(d => ScalarValue.MDouble(d)))).reads(json)
      case (key, ScalarType.Int) => (__ \ key).read[List[Int]].map(is => Attribute(key, is.map(i => ScalarValue.MInt(i)))).reads(json)
      case (key, ScalarType.String) => (__ \ key).read[List[String]].map(ss => Attribute(key, ss.map(s => ScalarValue.MString(s)))).reads(json)
      case (key, ScalarType.Bool) => (__ \ key).read[List[Boolean]].map(bs => Attribute(key, bs.map(b => ScalarValue.MBool(b)))).reads(json)
      case (key, MEnum(name, values)) => (__ \ key).read[List[String]].filter(ValidationError(s"Found elements aren't valid symbols of enum $name")) {
        _.forall(symbolString => values.exists(_.name == symbolString))
      }.map(sym => Attribute(key, sym.map(s => values.find(_.name == s).get))).reads(json)
      case _ => JsError("Unknown attribute type")
    }
  }

  def checkAttributeMult(a: Attribute, ma: MAttribute): Boolean = {
    val lowerBound = a.value.size >= ma.lowerBound
    val upperBound = a.value.size <= ma.upperBound || ma.upperBound == -1
    lowerBound && upperBound
  }

  def nodeReads(implicit meta: MetaModel): Reads[Node] = (
    (__ \ "id").read[String] and
    (__ \ "mClass").read[String].filter(unknownMClassError) {
      s => meta.containsMClass(s)
    }.map {
      s => meta.getMClass(s).get
    } and
    (__ \ "outputs").read[Map[String, Seq[String]]].filter(invalidToEdgesError) {
      e => e.keys.forall(s => meta.containsMReference(s))
    }.map(extractEdges) and
    (__ \ "inputs").read[Map[String, Seq[String]]].filter(invalidToEdgesError) {
      e => e.keys.forall(s => meta.containsMReference(s))
    }.map(extractEdges) and
    (__ \ "mClass").read[String].flatMap(name => (__ \ "attributes").read[List[Attribute]](attributesReads {
      meta.getMClass(name).map(c => c.getTypeMAttributes).getOrElse(Seq[MAttribute]())
    }))
  )(Node.apply2 _).filter(invalidToEdgesError2) {
    validateNodeLinks
  }.filter(attributeDuplicateError) {
    ensureUniqueAttributes
  }

  private def validateNodeLinks(n: Node) = {
    n.inputs.forall(e => n.`type`.typeHasInput(e.`type`.name)) &&
      n.outputs.forall(e => n.`type`.typeHasOutput(e.`type`.name))
  }

  private def validateEdgeLinks(e: Edge) = {
    e.source.forall(n => e.`type`.source.exists(l => n.`type`.typeHasSuperType(l.mType.name))) &&
      e.target.forall(n => e.`type`.target.exists(l => n.`type`.typeHasSuperType(l.mType.name)))
  }

  private def ensureUniqueAttributes(value: HasAttributes) = {
    value.attributes.map(_.name.toLowerCase).toSet.size == value.attributes.size
  }

  private def extractNodes(m: Map[String, Seq[String]])(implicit meta: MetaModel): Seq[ToNodes] = {
    m.map {
      case (k, v) =>
        val t = meta.elements(k).asInstanceOf[MClass]
        val n = v.map(emtpyNode)
        ToNodes(t, n)
    }
  }.toList

  def edgeReads(implicit meta: MetaModel): Reads[Edge] = (
    (__ \ "id").read[String] and
    (__ \ "mReference").read[String].filter(unknownMReferenceError) {
      s => meta.containsMReference(s)
    }.map {
      s => meta.getMReference(s).get
    } and
    (__ \ "source").read[Map[String, Seq[String]]].filter(invalidToNodesError) {
      n => n.keys.forall(s => meta.containsMClass(s))
    }.map(extractNodes) and
    (__ \ "target").read[Map[String, Seq[String]]].filter(invalidToNodesError) {
      n => n.keys.forall(s => meta.containsMClass(s))
    }.map(extractNodes) and
    (__ \ "attributes").read(Seq[Attribute]())
  )(Edge.apply2 _).filter(invalidToNodesError2) {
    validateEdgeLinks
  }.filter(attributeDuplicateError) {
    ensureUniqueAttributes
  }

}
