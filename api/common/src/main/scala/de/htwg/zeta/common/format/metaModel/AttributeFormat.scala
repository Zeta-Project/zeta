package de.htwg.zeta.common.format.metaModel

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat

class AttributeFormat(
    attributeTypeFormat: AttributeTypeFormat,
    attributeValueFormat: AttributeValueFormat,
    sName: String = "name",
    sGlobalUnique: String = "globalUnique",
    sLocalUnique: String = "localUnique",
    sType: String = "type",
    sDefault: String = "default",
    sConstant: String = "constant",
    sSingleAssignment: String = "singleAssignment",
    sExpression: String = "expression",
    sOrdered: String = "ordered",
    sTransient: String = "transient"
) extends OFormat[MAttribute] {

  override def writes(attribute: MAttribute): JsObject = Json.obj(
    sName -> attribute.name,
    sGlobalUnique -> attribute.globalUnique,
    sLocalUnique -> attribute.localUnique,
    sType -> attributeTypeFormat.writes(attribute.typ),
    sDefault -> attributeValueFormat.writes(attribute.default),
    sConstant -> attribute.constant,
    sSingleAssignment -> attribute.singleAssignment,
    sExpression -> attribute.expression,
    sOrdered -> attribute.ordered,
    sTransient -> attribute.transient
  )

  override def reads(json: JsValue): JsResult[MAttribute] = for {
    name <- (json \ sName).validate[String]
    globalUnique <- (json \ sGlobalUnique).validate[Boolean]
    localUnique <- (json \ sLocalUnique).validate[Boolean]
    typ <- (json \ sType).validate(attributeTypeFormat)
    default <- (json \ sDefault).validate(attributeValueFormat)
    constant <- (json \ sConstant).validate[Boolean]
    singleAssignment <- (json \ sSingleAssignment).validate[Boolean]
    expression <- (json \ sExpression).validate[String]
    ordered <- (json \ sOrdered).validate[Boolean]
    transient <- (json \ sTransient).validate[Boolean]
  } yield {
    MAttribute(
      name = name,
      globalUnique = globalUnique,
      localUnique = localUnique,
      typ = typ,
      default = default,
      constant = constant,
      singleAssignment = singleAssignment,
      expression = expression,
      ordered = ordered,
      transient = transient
    )
  }

}
