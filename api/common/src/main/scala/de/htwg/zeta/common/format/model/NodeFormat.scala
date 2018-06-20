package de.htwg.zeta.common.format.model

import de.htwg.zeta.common.format.project.AttributeFormat
import de.htwg.zeta.common.format.project.AttributeValueFormat
import de.htwg.zeta.common.format.project.MethodFormat
import de.htwg.zeta.common.models.project.instance.elements.NodeInstance
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class NodeFormat(
    attributeFormat: AttributeFormat,
    attributeValueFormat: AttributeValueFormat,
    methodFormat: MethodFormat,
    sName: String = "name",
    sClassName: String = "className",
    sOutputEdgeNames: String = "outputEdgeNames",
    sInputEdgeNames: String = "inputEdgeNames",
    sAttributes: String = "attributes",
    sAttributeValues: String = "attributeValues",
    sMethods: String = "methods"
) extends OFormat[NodeInstance] {

  override def writes(node: NodeInstance): JsObject = Json.obj(
    sName -> node.name,
    sClassName -> node.className,
    sOutputEdgeNames -> node.outputEdgeNames,
    sInputEdgeNames -> node.inputEdgeNames,
    sAttributes -> Writes.seq(attributeFormat).writes(node.attributes),
    sAttributeValues -> attributeValueFormat.asMapOfLists.writes(node.attributeValues),
    sMethods -> Writes.seq(methodFormat).writes(node.methods)
  )

  override def reads(json: JsValue): JsResult[NodeInstance] = for {
    name <- (json \ sName).validate[String]
    className <- (json \ sClassName).validate[String]
    outputs <- (json \ sOutputEdgeNames).validate(Reads.list[String])
    inputs <- (json \ sInputEdgeNames).validate(Reads.list[String])
    attributes <- (json \ sAttributes).validate(Reads.list(attributeFormat))
    attributeValues <- (json \ sAttributeValues).validate(attributeValueFormat.asMapOfLists)
    methods <- (json \ sMethods).validate(Reads.list(methodFormat))
  } yield {
    NodeInstance(
      name = name,
      className = className,
      outputEdgeNames = outputs,
      inputEdgeNames = inputs,
      attributes = attributes,
      attributeValues = attributeValues,
      methods = methods
    )
  }

}
