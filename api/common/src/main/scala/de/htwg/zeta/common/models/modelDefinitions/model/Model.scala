package de.htwg.zeta.common.models.modelDefinitions.model

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.Writes


/** Immutable container for model definitions
 *
 * @param name        the name of the model
 * @param metaModelId the id for the corresponding MetaModel
 * @param nodes       the nodes of the actual model data
 * @param edges       the edges of the actual model data
 * @param uiState     the ui-state of the browser client. Location is debatable
 */
case class Model(
    name: String,
    metaModelId: UUID,
    nodes: Seq[Node],
    edges: Seq[Edge],
    attributes: Map[String, Seq[AttributeValue]],
    uiState: String
) {

  /** Nodes mapped to their own names. */
  val nodeMap: Map[String, Node] = Option(nodes).fold(
    Map.empty[String, Node]
  ) { nodes =>
    nodes.filter(Option(_).isDefined).map(node => (node.name, node)).toMap
  }

  /** Edges mapped to their own names. */
  val edgeMap: Map[String, Edge] = Option(edges).fold(
    Map.empty[String, Edge]
  ) { edges =>
    edges.filter(Option(_).isDefined).map(edge => (edge.name, edge)).toMap
  }

}

object Model {

  def empty(name: String, metaModelId: UUID): Model = {
    Model(
      name = name,
      metaModelId = metaModelId,
      nodes = Seq.empty,
      edges = Seq.empty,
      attributes = Map.empty,
      uiState = ""
    )
  }

  private val sName = "name"
  private val sMetaModelId = "metaModelId"
  private val sNodes = "nodes"
  private val sEdges = "edges"
  private val sAttributes = "attributes"
  private val sUiState = "uiState"

  def playJsonReads(metaModelEntity: MetaModelEntity): Reads[Model] = new Reads[Model] {
    override def reads(json: JsValue): JsResult[Model] = {
      for {
        name <- (json \ sName).validate[String]
        nodes <- (json \ sNodes).validate(Reads.list(Node.playJsonReads(metaModelEntity.metaModel)))
        edges <- (json \ sEdges).validate(Reads.list(Edge.playJsonReads(metaModelEntity.metaModel)))
        attributes <- (json \ sAttributes).validate(AttributeValue.playJsonReads(metaModelEntity.metaModel, metaModelEntity.metaModel.attributes))
        uiState <- (json \ sUiState).validate[String]
      } yield {
        Model(
          name = name,
          metaModelId = metaModelEntity.id,
          nodes = nodes,
          edges = edges,
          attributes = attributes,
          uiState = uiState
        )
      }
    }
  }

  implicit val playJsonWrites: Writes[Model] = Json.writes[Model]

  val playJsonReadsEmpty: Reads[Model] = new Reads[Model] {
    override def reads(json: JsValue): JsResult[Model] = {
      for {
        name <- (json \ sName).validate[String]
        metaModelId <- (json \ sMetaModelId).validate[UUID]
      } yield {
        Model.empty(name, metaModelId)
      }
    }
  }

}
