package de.htwg.zeta.common.format.model

import de.htwg.zeta.common.format.metaModel.AttributeValueFormat
import de.htwg.zeta.common.format.metaModel.MAttributeFormat
import de.htwg.zeta.common.format.metaModel.MethodFormat
import de.htwg.zeta.common.format.model.EdgeFormat.sAttributes
import de.htwg.zeta.common.format.model.EdgeFormat.sAttributeValues
import de.htwg.zeta.common.format.model.EdgeFormat.sMethods
import de.htwg.zeta.common.format.model.EdgeFormat.sName
import de.htwg.zeta.common.format.model.EdgeFormat.sReferenceName
import de.htwg.zeta.common.format.model.EdgeFormat.sSource
import de.htwg.zeta.common.format.model.EdgeFormat.sTarget
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import play.api.libs.json.JsError
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.OWrites
import play.api.libs.json.Reads
import play.api.libs.json.Writes


object EdgeFormat extends OWrites[Edge] {

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
    sAttributes -> Writes.seq(MAttributeFormat).writes(edge.attributes),
    sAttributeValues -> Writes.map(AttributeValueFormat).writes(edge.attributeValues),
    sMethods -> Writes.seq(MethodFormat).writes(edge.methods)
  )

}

case class EdgeFormat(metaModel: MetaModel) extends Reads[Edge] {

  override def reads(json: JsValue): JsResult[Edge] = {
    for {
      name <- (json \ sName).validate[String]
      referenceName <- (json \ sReferenceName).validate[String].flatMap { referenceName =>
        metaModel.referenceMap.get(referenceName) match {
          case Some(_) => JsSuccess(referenceName)
          case None => JsError(s"Unknown referenceName $referenceName")
        }
      }
      source <- (json \ sSource).validate[String]
      target <- (json \ sTarget).validate[String]
      attributes <- (json \ sAttributes).validate(Reads.list(MAttributeFormat(metaModel.enums)))
      attributeValues <- (json \ sAttributeValues).validate(Reads.map(AttributeValueFormat(metaModel.enums)))
      methods <- (json \ sMethods).validate(Reads.list(MethodFormat(metaModel.enums)))
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

