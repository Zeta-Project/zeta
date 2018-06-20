package de.htwg.zeta.common.format.model

import java.util.UUID

import de.htwg.zeta.common.format.project.AttributeFormat
import de.htwg.zeta.common.format.project.AttributeValueFormat
import de.htwg.zeta.common.format.project.MethodFormat
import de.htwg.zeta.common.models.project.instance
import de.htwg.zeta.common.models.project.instance
import de.htwg.zeta.common.models.project.instance.GraphicalDslInstance
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class GraphicalDslInstanceFormat(
    nodeFormat: NodeFormat,
    edgeFormat: EdgeFormat,
    attributeFormat: AttributeFormat,
    attributeValueFormat: AttributeValueFormat,
    methodFormat: MethodFormat,
    sId: String = "id",
    sName: String = "name",
    sGraphicalDslId: String = "graphicalDslId",
    sNodes: String = "nodes",
    sEdges: String = "edges",
    sAttributes: String = "attributes",
    sAttributeValues: String = "attributeValues",
    sMethods: String = "methods",
    sUiState: String = "uiState"
) extends OFormat[GraphicalDslInstance] {

  override def writes(instance: GraphicalDslInstance): JsObject = Json.obj(
    sId -> instance.id,
    sName -> instance.name,
    sGraphicalDslId -> instance.graphicalDslId,
    sNodes -> Writes.seq(nodeFormat).writes(instance.nodes),
    sEdges -> Writes.seq(edgeFormat).writes(instance.edges),
    sAttributes -> Writes.seq(attributeFormat).writes(instance.attributes),
    sAttributeValues -> attributeValueFormat.asMapOfLists.writes(instance.attributeValues),
    sMethods -> Writes.seq(methodFormat).writes(instance.methods),
    sUiState -> instance.uiState
  )

  override def reads(json: JsValue): JsResult[GraphicalDslInstance] = for {
    id <- (json \ sId).validate[UUID]
    name <- (json \ sName).validate[String]
    graphicalDslId <- (json \ sGraphicalDslId).validate[UUID]
    nodes <- (json \ sNodes).validate(Reads.list(nodeFormat))
    edges <- (json \ sEdges).validate(Reads.list(edgeFormat))
    attributes <- (json \ sAttributes).validate(Reads.list(attributeFormat))
    attributeValues <- (json \ sAttributeValues).validate(attributeValueFormat.asMapOfLists)
    methods <- (json \ sMethods).validate(Reads.list(methodFormat))
    uiState <- (json \ sUiState).validate[String]
  } yield {
    instance.GraphicalDslInstance(
      id = id,
      name = name,
      graphicalDslId = graphicalDslId,
      nodes = nodes,
      edges = edges,
      attributes = attributes,
      attributeValues = attributeValues,
      methods = methods,
      uiState = uiState
    )
  }

  val empty: Reads[GraphicalDslInstance] = Reads { json =>
    for {
      name <- (json \ sName).validate[String]
      metaModelId <- (json \ sGraphicalDslId).validate[UUID]
    } yield {
      GraphicalDslInstance.empty(name, metaModelId)
    }
  }

  def withId(id: UUID): Reads[GraphicalDslInstance] = Reads { json =>
    for {
      name <- (json \ sName).validate[String]
      graphicalDslId <- (json \ sGraphicalDslId).validate[UUID]
      nodes <- (json \ sNodes).validate(Reads.list(nodeFormat))
      edges <- (json \ sEdges).validate(Reads.list(edgeFormat))
      attributes <- (json \ sAttributes).validate(Reads.list(attributeFormat))
      attributeValues <- (json \ sAttributeValues).validate(attributeValueFormat.asMapOfLists)
      methods <- (json \ sMethods).validate(Reads.list(methodFormat))
      uiState <- (json \ sUiState).validate[String]
    } yield {
      instance.GraphicalDslInstance(
        id = id,
        name = name,
        graphicalDslId = graphicalDslId,
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
