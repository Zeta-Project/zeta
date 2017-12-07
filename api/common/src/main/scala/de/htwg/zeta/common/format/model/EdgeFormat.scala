package de.htwg.zeta.common.format.model

import de.htwg.zeta.common.format.metaModel.AttributeValueFormat
import de.htwg.zeta.common.format.metaModel.AttributeFormat
import de.htwg.zeta.common.format.metaModel.MethodFormat
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes


object EdgeFormat extends OFormat[Edge] {

  private val sName = "name"
  private val sReferenceName = "referenceName"
  private val sSource = "source"
  private val sTarget = "target"
  private val sAttributes = "attributes"
  private val sAttributeValues = "attributeValues"
  private val sMethods = "methods"

  override def writes(edge: Edge): JsObject = Json.obj(
    sName -> edge.name,
    sReferenceName -> edge.referenceName,
    sSource -> edge.sourceNodeName,
    sTarget -> edge.targetNodeName,
    sAttributes -> Writes.seq(AttributeFormat).writes(edge.attributes),
    sAttributeValues -> Writes.map(AttributeValueFormat).writes(edge.attributeValues),
    sMethods -> Writes.seq(MethodFormat).writes(edge.methods)
  )

  override def reads(json: JsValue): JsResult[Edge] = {
    for {
      name <- (json \ sName).validate[String]
      referenceName <- (json \ sReferenceName).validate[String]
      source <- (json \ sSource).validate[String]
      target <- (json \ sTarget).validate[String]
      attributes <- (json \ sAttributes).validate(Reads.list(AttributeFormat))
      attributeValues <- (json \ sAttributeValues).validate(Reads.map(AttributeValueFormat))
      methods <- (json \ sMethods).validate(Reads.list(MethodFormat))
    } yield {
      Edge(
        name = name,
        referenceName = referenceName,
        sourceNodeName = source,
        targetNodeName = target,
        attributes = attributes,
        attributeValues = attributeValues,
        methods = methods
      )
    }
  }

}

