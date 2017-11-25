package de.htwg.zeta.common.format.model

import java.util.UUID

import scala.collection.immutable.List
import scala.collection.immutable.Seq
import scala.collection.mutable
import scala.concurrent.Future

import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.EdgeLink
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.NodeLink
import play.api.libs.json.JsArray
import play.api.libs.json.JsError
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes


class ModelFormat private(userID: UUID) extends Reads[Future[JsResult[Model]]] with Writes[Model] {

  private val modelElementsNotUnique = JsError("elements must have unique names")

  private val repo: Repository = Persistence.restrictedAccessRepository(userID)

  private def check(unchecked: JsResult[Model]): JsResult[Model] = {
    unchecked.flatMap(model => {
      val set: mutable.HashSet[String] = mutable.HashSet()
      if (model.nodes.forall(n => set.add(n.name)) && model.edges.forall(e => set.add(e.name))) {
        JsSuccess(model)
      } else {
        modelElementsNotUnique
      }
    }).flatMap(checkLinks)
  }


  private def checkLinks(model: Model): JsResult[Model] = {
    val edgesMap = model.edges.map(e => (e.name, e)).toMap
    val nodesMap = model.nodes.map(n => (n.name, n)).toMap

    def checkGenericLink[T](source: String, map: Map[String, _], sup: T => Seq[String])(t: T): List[String] = {
      sup(t).toStream.flatMap(n => {
        if (map.contains(n)) {
          Nil
        } else {
          List(s"invalid link to node: '$source' -> '$n' ('$n' is missing or doesn't match expected type)")
        }
      }).headOption.toList
    }

    def checkNodes(n: Node): Option[String] = {
      val checkToEdge = checkGenericLink[EdgeLink](n.name, edgesMap, _.edgeNames) _
      n.inputs.toStream.flatMap(checkToEdge).headOption match {
        case None => n.outputs.toStream.flatMap(checkToEdge).headOption
        case some @ Some(_) => some
      }
    }


    def checkEdges(e: Edge): Option[String] = {
      val checkToNode = checkGenericLink[NodeLink](e.name, nodesMap, _.nodeNames) _
      e.source.toStream.flatMap(checkToNode).headOption match {
        case None => e.target.toStream.flatMap(checkToNode).headOption
        case some @ Some(_) => some
      }
    }

    val ret = model.nodes.toStream.flatMap(checkNodes(_).toList).headOption match {
      case None => model.edges.toStream.flatMap(checkEdges(_).toList).headOption
      case some @ Some(_) => some
    }

    ret match {
      case Some(error) => JsError(error)
      case None => JsSuccess(model)
    }
  }

  override def reads(json: JsValue): JsResult[Future[JsResult[Model]]] = {
    for {
      name <- (json \ "name").validate[String]
      metaModelId <- (json \ "metaModelId").validate[UUID]
    } yield {
      repo.metaModelEntity.read(metaModelId).map(entity => {
        val unchecked: JsResult[Model] =
          for {
            elements <- (json \ "elements").validate(Reads.list(ModelElementReads(entity.metaModel)))
            attributes <- (json \ "attributes").validate(AttributeFormat(entity.metaModel.attributes, name)) // TODO
            uiState <- (json \ "uiState").validate[String]
          } yield {
            val (nodes, edges) =
              elements.reverse.foldLeft((List[Node](), List[Edge]()))((pair, either) => either match {
                case Left(node) => (node :: pair._1, pair._2)
                case Right(edge) => (pair._1, edge :: pair._2)
              })
            Model(name, metaModelId, nodes, edges, Seq.empty, Map.empty, Seq.empty, uiState)
          }
        check(unchecked)
      })
    }
  }

  override def writes(o: Model): JsValue = ModelFormat.writes(o)
}

object ModelFormat extends Writes[Model] {

  def apply(userID: UUID): ModelFormat = new ModelFormat(userID)

  override def writes(o: Model): JsValue = {
    val elements = JsArray(o.nodes.map(NodeFormat.writes) ++ o.edges.map(EdgeFormat.writes))

    Json.obj(
      "name" -> o.name,
      "metaModelId" -> o.metaModelId,
      "elements" -> elements,
      "uiState" -> o.uiState
    )
  }
}
