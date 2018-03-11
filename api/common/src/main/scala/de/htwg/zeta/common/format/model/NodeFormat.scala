package de.htwg.zeta.common.format.model

import de.htwg.zeta.common.format.metaModel.AttributeFormat
import de.htwg.zeta.common.format.metaModel.AttributeValueFormat
import de.htwg.zeta.common.format.metaModel.MethodFormat
import de.htwg.zeta.common.models.project.instance.elements.Node
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
) extends OFormat[Node] {

  override def writes(node: Node): JsObject = Json.obj(
    sName -> node.name,
    sClassName -> node.className,
    sOutputEdgeNames -> node.outputEdgeNames,
    sInputEdgeNames -> node.inputEdgeNames,
    sAttributes -> Writes.seq(attributeFormat).writes(node.attributes),
    sAttributeValues -> Writes.map(attributeValueFormat).writes(node.attributeValues),
    sMethods -> Writes.seq(methodFormat).writes(node.methods)
  )

  override def reads(json: JsValue): JsResult[Node] = for {
    name <- (json \ sName).validate[String]
    className <- (json \ sClassName).validate[String]
    outputs <- (json \ sOutputEdgeNames).validate(Reads.list[String])
    inputs <- (json \ sInputEdgeNames).validate(Reads.list[String])
    attributes <- (json \ sAttributes).validate(Reads.list(attributeFormat))
    attributeValues <- (json \ sAttributeValues).validate(Reads.map(attributeValueFormat))
    methods <- (json \ sMethods).validate(Reads.list(methodFormat))
  } yield {
    Node(
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
