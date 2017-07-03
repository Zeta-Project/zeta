package de.htwg.zeta.server.controller.restApi.modelUiFormat

import java.util.UUID

import scala.collection.mutable
import scala.collection.immutable.List
import scala.collection.immutable.Seq
import scala.concurrent.Future

import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToNodes
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToEdges
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.persistence.Persistence
import de.htwg.zeta.persistence.general.Repository
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.Json
import play.api.libs.json.JsSuccess
import play.api.libs.json.Writes
import play.api.libs.json.JsResult
import play.api.libs.json.JsError
import play.api.libs.json.JsArray
import scala.concurrent.ExecutionContext.Implicits.global


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
            elements <- (json \ "elements").validate(Reads.list(ModelElementReads(entity.metaModel)))
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
