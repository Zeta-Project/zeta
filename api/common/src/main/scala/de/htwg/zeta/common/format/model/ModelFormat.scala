package de.htwg.zeta.common.format.model

import java.util.UUID

import de.htwg.zeta.common.format.metaModel.AttributeValueFormat
import de.htwg.zeta.common.format.metaModel.MAttributeFormat
import de.htwg.zeta.common.format.metaModel.MethodFormat
import de.htwg.zeta.common.format.model.ModelFormat.sAttributes
import de.htwg.zeta.common.format.model.ModelFormat.sAttributeValues
import de.htwg.zeta.common.format.model.ModelFormat.sEdges
import de.htwg.zeta.common.format.model.ModelFormat.sMethods
import de.htwg.zeta.common.format.model.ModelFormat.sName
import de.htwg.zeta.common.format.model.ModelFormat.sNodes
import de.htwg.zeta.common.format.model.ModelFormat.sUiState
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OWrites
import play.api.libs.json.Reads
import play.api.libs.json.Writes


object ModelFormat extends OWrites[Model] {

  private val sName = "name"
  private val sMetaModelId = "metaModelId"
  private val sNodes = "nodes"
  private val sEdges = "edges"
  private val sAttributes = "attributes"
  private val sAttributeValues = "attributeValues"
  private val sMethods = "methods"
  private val sUiState = "uiState"


  override def writes(model: Model): JsObject = Json.obj(
    sName -> model.name,
    sMetaModelId -> model.metaModelId,
    sNodes -> Writes.seq(NodeFormat).writes(model.nodes),
    sEdges -> Writes.seq(EdgeFormat).writes(model.edges),
    sAttributes -> Writes.seq(MAttributeFormat).writes(model.attributes),
    sAttributeValues -> Writes.map(AttributeValueFormat).writes(model.attributeValues),
    sMethods -> Writes.seq(MethodFormat).writes(model.methods),
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

}

class ModelFormat(metaModelId: UUID, metaModel: MetaModel) extends Reads[Model] {

  override def reads(json: JsValue): JsResult[Model] = {
    for {
      name <- (json \ sName).validate[String]
      nodes <- (json \ sNodes).validate(Reads.list(new NodeFormat(metaModel)))
      edges <- (json \ sEdges).validate(Reads.list(new EdgeFormat(metaModel)))
      attributes <- (json \ sAttributes).validate(Reads.list(new MAttributeFormat(metaModel.enums)))
      attributeValues <- (json \ sAttributeValues).validate(Reads.map(new AttributeValueFormat(metaModel.enums)))
      methods <- (json \ sMethods).validate(Reads.list(new MethodFormat(metaModel.enums)))
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

}
