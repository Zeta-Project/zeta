package de.htwg.zeta.server.controller.restApi.modelUiFormat


import java.util.UUID

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.immutable.Seq
import scala.collection.immutable.List
import scala.concurrent.Future

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClassLinkDef
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MBool
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MDouble
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumSymbol
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToEdges
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToNodes
import de.htwg.zeta.persistence.Persistence
import play.api.libs.json.JsError
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsResult
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsNumber

import scala.concurrent.ExecutionContext.Implicits.global


/**
 */
object ModelUiFormat {

  def futureReads(userID: UUID, json: JsValue): Future[JsResult[Model]] = {
    json.validate(ModelReads(userID)) match {
      case JsSuccess(futureRes, _) => futureRes
      case e: JsError => Future.successful(e)
    }
  }

  case class ModelReads(userID: UUID) extends Reads[Future[JsResult[Model]]] {

    private val modelElementsNotUnique = JsError("elements must have unique names")

    private val repo = Persistence.restrictedAccessRepository(userID)

    private def check(unchecked: JsResult[Model]): JsResult[Model] = {
      unchecked.flatMap(model => {
        val set: mutable.HashSet[String] = mutable.HashSet()
        if (model.nodes.forall(n => set.add(n.name)) && model.edges.forall(e => set.add(e.name))) {
          JsSuccess(model)
        } else {
          modelElementsNotUnique
        }
      }).flatMap((model: Model) => {
        // TODO FIXME nicolas make this great again.

        val nodes = model.nodes
        val edges = model.edges

        def flatEdge(source: String, seq: Seq[ToEdges]): Stream[(String, String)] = seq.toStream.flatMap(_.edgeNames).map(t => (source, t))

        def flatNode(source: String, seq: Seq[ToNodes]): Stream[(String, String)] = seq.toStream.flatMap(_.nodeNames).map(t => (source, t))

        val toEdges: Stream[(String, String)] = nodes.toStream.flatMap(n => Stream(flatEdge(n.name, n.inputs), flatEdge(n.name, n.outputs)).flatten)
        val toNodes: Stream[(String, String)] = edges.toStream.flatMap(e => Stream(flatNode(e.name, e.source), flatNode(e.name, e.target)).flatten)

        val edgesMap = edges.map(e => (e.name, e)).toMap
        val nodesMap = nodes.map(n => (n.name, n)).toMap


        toEdges.find(p => !edgesMap.contains(p._2)).map(p =>
          s"invalid link to edge: '${p._1}' -> '${p._2}' ('${p._2}' is missing or doesn't match expected type)"
        ).orElse {
          toNodes.find(p => !nodesMap.contains(p._2)).map(p =>
            s"invalid link to node: '${p._1}' -> '${p._2}' ('${p._2}' is missing or doesn't match expected type)"
          )
        } match {
          case Some(error) => JsError(error)
          case None => JsSuccess(model)
        }
      })
    }

    override def reads(json: JsValue): JsResult[Future[JsResult[Model]]] = {
      for {
        name <- (json \ "name").validate[String]
        metaModelId <- (json \ "metaModelId").validate[UUID]
      } yield {
        repo.metaModelEntity.read(metaModelId).map(entity => {
          val unchecked: JsResult[Model] =
            for {
              elements <- (json \ "elements").validate(Reads.list(new ModelElementReads(entity.metaModel)))
              uiState <- (json \ "uiState").validate[String]
            } yield {
              val (nodes, edges) =
                elements.reverse.foldLeft((List[Node](), List[Edge]()))((pair, either) => either match {
                  case Left(node) => (node :: pair._1, pair._2)
                  case Right(edge) => (pair._1, edge :: pair._2)
                })
              Model(name, metaModelId, nodes, edges, uiState)
            }
          check(unchecked)
        })
      }
    }
  }

  class ModelElementReads(metaModel: MetaModel) extends Reads[Either[Node, Edge]] {
    val nodeReads = new NodeReads(metaModel)
    val edgeReads = new EdgeReads(metaModel)

    override def reads(json: JsValue): JsResult[Either[Node, Edge]] = {
      def hasKey(name: String) = (json \ name).toOption.isDefined

      if (hasKey("mClass")) {
        json.validate(nodeReads).map(Left(_))
      } else if (hasKey("mReference")) {
        json.validate(edgeReads).map(Right(_))
      } else {
        JsError(s"Unknown type. json: $json is neither a Node nor an Edge")
      }
    }
  }

  class NodeReads(metaModel: MetaModel) extends Reads[Node] {
    val unknownMClassError = JsError("Unknown mClass")
    val invalidToEdgesError = JsError("edge reference has invalid type")

    def extractEdges(typeHas: String => Boolean)(m: Map[String, Seq[String]]): JsResult[List[ToEdges]] = {
      m.toList.reverse.foldLeft[JsResult[List[ToEdges]]](JsSuccess(Nil))((res, kv) => {
        res match {
          case JsSuccess(list, _) =>
            val (k, v) = kv
            metaModel.referenceMap.get(k) match {
              case Some(t: MReference) if typeHas(t.name) => JsSuccess(ToEdges(t, v) :: list)
              case None => invalidToEdgesError
            }
          case e: JsError => e
        }
      })
    }

    override def reads(json: JsValue): JsResult[Node] = {

      for {
        name <- (json \ "id").validate[String]
        clazz <- (json \ "mClass").validate[String].flatMap(className => metaModel.classMap.get(className) match {
          case Some(mClass) => JsSuccess(mClass)
          case None => unknownMClassError
        })
        traverse = MClass.MClassTraverseWrapper(clazz, MetaModel.MetaModelTraverseWrapper(metaModel))
        output <- (json \ "outputs").validate[Map[String, Seq[String]]].flatMap(extractEdges(traverse.typeHasOutput))
        input <- (json \ "inputs").validate[Map[String, Seq[String]]].flatMap(extractEdges(traverse.typeHasInput))
        attr <- (json \ "attributes").validate(new AttributeReads(clazz.attributes, name))

      } yield {
        Node(name, clazz, output, input, attr)
      }
    }
  }

  class AttributeReads(mAttributes: Seq[MAttribute], objectName: String) extends Reads[Map[String, List[AttributeValue]]] {
    private val attributeBoundsCheck = JsError("Attribute bounds check failed")

    // used to validate JsLookup. JsValue has flatMap. JsLookup hasn't
    private object ToJsResult extends Reads[JsValue] {
      override def reads(json: JsValue): JsResult[JsValue] = JsSuccess(json)
    }

    private def parseList[AV](jsTypeAsString: String)(pf: PartialFunction[JsValue, JsResult[AV]])(jsValues: List[JsValue]): JsResult[List[AV]] = {
      @tailrec
      def fold(res: JsResult[List[AV]], jsList: List[JsValue]): JsResult[List[AV]] = {
        (res, jsList) match {
          case (e: JsError, _) => e
          case (s @ JsSuccess(_, _), Nil) => s
          case (JsSuccess(list, _), jsv :: tail) =>
            if (pf.isDefinedAt(jsv)) {
              fold(pf(jsv).map(_ :: list), tail)
            } else {
              JsError(s"Json: $jsv is not of type $jsTypeAsString")
            }
        }
      }

      fold(JsSuccess(Nil), jsValues).map(_.reverse)
    }

    private object CheckValidInt {
      def unapply(jsn: JsNumber): Option[Int] = try {
        Some(jsn.value.toIntExact)
      } catch {
        case _: java.lang.ArithmeticException => None
      }
    }

    private def checkAttributeBounds(la: List[AttributeValue], ma: MAttribute): Boolean = {
      val laSize = la.size
      val lowerBound = laSize >= ma.lowerBound
      val upperBound = laSize <= ma.upperBound || ma.upperBound == -1
      lowerBound && upperBound
    }

    // scalastyle:off
    // Cyclomatic complexety is greater 10
    private def parseAttribute(json: JsValue, ma: MAttribute): JsResult[(String, List[AttributeValue])] = {
      val jsValues: JsResult[List[JsValue]] = json.\(ma.name).validate(Reads.list(ToJsResult))
      val attributeValues: JsResult[List[AttributeValue]] = ma.typ match {
        case StringType => jsValues.flatMap(parseList("JsString") { case JsString(s) => JsSuccess(MString(s)) })
        case BoolType => jsValues.flatMap(parseList("JsBoolean") { case JsBoolean(b) => JsSuccess(MBool(b)) })
        case IntType => jsValues.flatMap(parseList("JsNumber") { case CheckValidInt(i) => JsSuccess(MInt(i)) })
        case DoubleType => jsValues.flatMap(parseList("JsNumber") { case JsNumber(n) => JsSuccess(MDouble(n.toDouble)) })
        case MEnum(name, values) =>
          val set = values.toSet
          jsValues.flatMap(parseList("JsString") {
            case JsString(s) =>
              if (set.contains(s)) {
                JsSuccess(EnumSymbol(s, name))
              } else {
                JsError(s"Found element: $s isn't a valid symbol of enum $name")
              }
          })
      }
      attributeValues.flatMap(l =>
        if (checkAttributeBounds(l, ma)) {
          JsSuccess((ma.name, l))
        } else {
          attributeBoundsCheck
        }
      )
    }
    // scalastyle:on


    override def reads(json: JsValue): JsResult[Map[String, List[AttributeValue]]] = {
      @tailrec
      def fold(res: JsResult[List[(String, List[AttributeValue])]], attrList: List[MAttribute]): JsResult[List[(String, List[AttributeValue])]] = {
        (res, attrList) match {
          case (e: JsError, _) => e
          case (s @ JsSuccess(_, _), Nil) => s
          case (JsSuccess(list, _), ma :: tail) =>
            fold(parseAttribute(json, ma).map(_ :: list), tail)
        }
      }

      val jsRes: JsResult[List[(String, List[AttributeValue])]] = fold(JsSuccess(Nil), mAttributes.toList)
      val set: mutable.HashSet[String] = mutable.HashSet()
      jsRes.flatMap(list => list.find(elem => !set.add(elem._1.toLowerCase)) match {
        case Some(e) => JsError(s"object: $objectName may contain the same attribute only once. Attributes are case insensitive, duplicate is: ${e._1}")
        case None => JsSuccess(list.toMap)
      })
    }
  }


  class EdgeReads(metaModel: MetaModel) extends Reads[Edge] {
    val invalidToNodesError = JsError("node reference has invalid type 2")
    val unknownMReferenceError = JsError("Unknown mReference")


    def extractToNodes(classLinks: Seq[MClassLinkDef])(m: Map[String, Seq[String]]): JsResult[List[ToNodes]] = {
      m.toList.reverse.foldLeft[JsResult[List[ToNodes]]](JsSuccess(Nil))((res, kv) => {
        res match {
          case JsSuccess(list, _) =>
            val (k, v) = kv
            metaModel.classMap.get(k) match {
              case Some(t: MClass) if classLinks.exists(mLink => mLink.className == t.name) =>
                JsSuccess(ToNodes(t, v) :: list)
              case None => invalidToNodesError
            }
          case e: JsError => e
        }
      })
    }

    override def reads(json: JsValue): JsResult[Edge] = {
      for {
        name <- json.\("id").validate[String]
        mReference <- json.\("mReference").validate[String].flatMap(refName => metaModel.referenceMap.get(refName) match {
          case Some(mRef) => JsSuccess(mRef)
          case None => unknownMReferenceError
        })
        // Todo not sure if check is correct or needs to be swapped
        source <- json.\("source").validate[Map[String, Seq[String]]].flatMap(extractToNodes(mReference.source))
        target <- json.\("target").validate[Map[String, Seq[String]]].flatMap(extractToNodes(mReference.target))
        attributes <- json.\("attributes").validate(new AttributeReads(mReference.attributes, name))
      } yield {
        Edge(name, mReference, source, target, attributes)
      }
    }
  }

}
