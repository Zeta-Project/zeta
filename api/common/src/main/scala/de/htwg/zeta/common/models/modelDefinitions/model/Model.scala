package de.htwg.zeta.common.models.modelDefinitions.model

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute.AttributeMap
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method.MethodMap
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge.EdgeMap
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node.NodeMap
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
    attributes: Seq[MAttribute],
    attributeValues: Map[String, Seq[AttributeValue]],
    methods: Seq[Method],
    uiState: String
) extends NodeMap with EdgeMap with AttributeMap with MethodMap

object Model {

  def empty(name: String, metaModelId: UUID): Model = {
    Model(
      name = name,
      metaModelId = metaModelId,
      nodes = Seq.empty,
      edges = Seq.empty,
      attributes = Seq.empty,
      attributeValues = Map.empty,
      methods = Seq.empty,
      uiState = ""
    )
  }

  private val sName = "name"
  private val sMetaModelId = "metaModelId"
  private val sNodes = "nodes"
  private val sEdges = "edges"
  private val sUiState = "uiState"

  def playJsonReads(metaModelEntity: MetaModelEntity): Reads[Model] = new Reads[Model] {
    val metaModel: MetaModel = metaModelEntity.metaModel

    override def reads(json: JsValue): JsResult[Model] = {
      for {
        name <- (json \ sName).validate[String]
        nodes <- (json \ sNodes).validate(Reads.list(Node.playJsonReads(metaModel)))
        edges <- (json \ sEdges).validate(Reads.list(Edge.playJsonReads(metaModel)))
        attributes <- (json \ "attributes").validate(Reads.list(MAttribute.playJsonReads(metaModel.enums)))
        attributeValues <- (json \ "attributeValues").validate(AttributeValue.playJsonReads(metaModel, metaModel.attributes, attributes))
        methods <- (json \ "methods").validate(Reads.list(Method.playJsonReads(metaModel.enums)))
        uiState <- (json \ sUiState).validate[String]
      } yield {
        Model(
          name = name,
          metaModelId = metaModelEntity.id,
          nodes = nodes,
          edges = edges,
          attributes = attributes,
          attributeValues = attributeValues,
          methods = methods,
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
