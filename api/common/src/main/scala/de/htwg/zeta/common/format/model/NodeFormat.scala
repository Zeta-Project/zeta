package de.htwg.zeta.common.format.model

import de.htwg.zeta.common.format.metaModel.AttributeValueFormat
import de.htwg.zeta.common.format.metaModel.MAttributeFormat
import de.htwg.zeta.common.format.metaModel.MethodFormat
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import play.api.libs.json.JsError
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.OWrites
import play.api.libs.json.Reads
import play.api.libs.json.Writes


object NodeFormat extends OWrites[Node] {

  private val sName = "name"
  private val sClassName = "className"
  private val sOutputEdgeNames = "outputEdgeNames"
  private val sInputEdgeNames = "inputEdgeNames"
  private val sAttributes = "attributes"
  private val sAttributeValues = "attributeValues"
  private val sMethods = "methods"

  override def writes(node: Node): JsObject = Json.obj(
    sName -> node.name,
    sClassName -> node.className,
    sOutputEdgeNames -> node.outputEdgeNames,
    sInputEdgeNames -> node.inputEdgeNames,
    sAttributes -> Writes.seq(MAttributeFormat).writes(node.attributes),
    sAttributeValues -> Writes.map(AttributeValueFormat).writes(node.attributeValues),
    sMethods -> Writes.seq(MethodFormat).writes(node.methods)
  )

}

class NodeFormat(metaModel: MetaModel) extends Reads[Node] {

  override def reads(json: JsValue): JsResult[Node] = {
    for {
      name <- (json \ NodeFormat.sName).validate[String]
      className <- (json \ NodeFormat.sClassName).validate[String].flatMap { className =>
        metaModel.classMap.get(className) match {
          case Some(_) => JsSuccess(className)
          case None => JsError(s"Unknown className $className")
        }
      }
      outputs <- (json \ NodeFormat.sOutputEdgeNames).validate(Reads.list[String])
      inputs <- (json \ NodeFormat.sInputEdgeNames).validate(Reads.list[String])
      attributes <- (json \ NodeFormat.sAttributes).validate(Reads.list(new MAttributeFormat(metaModel.enums)))
      attributeValues <- (json \ NodeFormat.sAttributeValues).validate(Reads.map(new AttributeValueFormat(metaModel.enums)))
      methods <- (json \ NodeFormat.sMethods).validate(Reads.list(new MethodFormat(metaModel.enums)))
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

}
