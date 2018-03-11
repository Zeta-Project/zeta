package de.htwg.zeta.common.format.model

import de.htwg.zeta.common.format.metaModel.AttributeValueFormat
import de.htwg.zeta.common.format.metaModel.AttributeFormat
import de.htwg.zeta.common.format.metaModel.MethodFormat
import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class EdgeFormat(
    attributeFormat: AttributeFormat,
    attributeValueFormat: AttributeValueFormat,
    methodFormat: MethodFormat,
    sName: String = "name",
    sReferenceName: String = "referenceName",
    sSourceNodeName: String = "sourceNodeName",
    sTargetNodeName: String = "targetNodeName",
    sAttributes: String = "attributes",
    sAttributeValues: String = "attributeValues",
    sMethods: String = "methods"
) extends OFormat[EdgeInstance] {

  override def writes(edge: EdgeInstance): JsObject = Json.obj(
    sName -> edge.name,
    sReferenceName -> edge.referenceName,
    sSourceNodeName -> edge.sourceNodeName,
    sTargetNodeName -> edge.targetNodeName,
    sAttributes -> Writes.seq(attributeFormat).writes(edge.attributes),
    sAttributeValues -> Writes.map(attributeValueFormat).writes(edge.attributeValues),
    sMethods -> Writes.seq(methodFormat).writes(edge.methods)
  )

  override def reads(json: JsValue): JsResult[EdgeInstance] = for {
    name <- (json \ sName).validate[String]
    referenceName <- (json \ sReferenceName).validate[String]
    source <- (json \ sSourceNodeName).validate[String]
    target <- (json \ sTargetNodeName).validate[String]
    attributes <- (json \ sAttributes).validate(Reads.list(attributeFormat))
    attributeValues <- (json \ sAttributeValues).validate(Reads.map(attributeValueFormat))
    methods <- (json \ sMethods).validate(Reads.list(methodFormat))
  } yield {
    EdgeInstance(
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

