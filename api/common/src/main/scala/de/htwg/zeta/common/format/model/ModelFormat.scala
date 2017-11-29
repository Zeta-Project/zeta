package de.htwg.zeta.common.format.model

import java.util.UUID

import de.htwg.zeta.common.format.metaModel.AttributeFormat
import de.htwg.zeta.common.format.metaModel.AttributeValueFormat
import de.htwg.zeta.common.format.metaModel.MethodFormat
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes


class ModelFormat(
    nodeFormat: NodeFormat,
    edgeFormat: EdgeFormat,
    attributeFormat: AttributeFormat,
    attributeValueFormat: AttributeValueFormat,
    methodFormat: MethodFormat,
    sName: String = "name",
    sMetaModelId: String = "metaModelId",
    sNodes: String = "nodes",
    sEdges: String = "edges",
    sAttributes: String = "attributes",
    sAttributeValues: String = "attributeValues",
    sMethods: String = "methods",
    sUiState: String = "uiState"
) extends OFormat[Model] {

  override def writes(model: Model): JsObject = Json.obj(
    sName -> model.name,
    sMetaModelId -> model.metaModelId,
    sNodes -> Writes.seq(nodeFormat).writes(model.nodes),
    sEdges -> Writes.seq(edgeFormat).writes(model.edges),
    sAttributes -> Writes.seq(attributeFormat).writes(model.attributes),
    sAttributeValues -> Writes.map(attributeValueFormat).writes(model.attributeValues),
    sMethods -> Writes.seq(methodFormat).writes(model.methods),
    sUiState -> model.uiState
  )

  val empty: Reads[Model] = Reads { json =>
    for {
      name <- (json \ sName).validate[String]
      metaModelId <- (json \ sMetaModelId).validate[UUID]
    } yield {
      Model.empty(name, metaModelId)
    }
  }

  override def reads(json: JsValue): JsResult[Model] = for {
    name <- (json \ sName).validate[String]
    metaModelId <- (json \ sMetaModelId).validate[UUID]
    nodes <- (json \ sNodes).validate(Reads.list(nodeFormat))
    edges <- (json \ sEdges).validate(Reads.list(edgeFormat))
    attributes <- (json \ sAttributes).validate(Reads.list(attributeFormat))
    attributeValues <- (json \ sAttributeValues).validate(Reads.map(attributeValueFormat))
    methods <- (json \ sMethods).validate(Reads.list(methodFormat))
    uiState <- (json \ sUiState).validate[String]
  } yield {
    Model(
      name = name,
      metaModelId = metaModelId,
      nodes = nodes,
      edges = edges,
      attributes = attributes,
      attributeValues = attributeValues,
      methods = methods,
      uiState = uiState
    )
  }

}
