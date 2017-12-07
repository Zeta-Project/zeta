package de.htwg.zeta.common.format.metaModel

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat

object AttributeFormat extends OFormat[MAttribute] {

  private val sName = "name"
  private val sGlobalUnique = "globalUnique"
  private val sLocalUnique = "localUnique"
  private val sType = "type"
  private val sDefault = "default"
  private val sConstant = "constant"
  private val sSingleAssignment = "singleAssignment"
  private val sExpression = "expression"
  private val sOrdered = "ordered"
  private val sTransient = "transient"

  override def writes(attribute: MAttribute): JsObject = Json.obj(
    sName -> attribute.name,
    sGlobalUnique -> attribute.globalUnique,
    sLocalUnique -> attribute.localUnique,
    sType -> AttributeTypeFormat.writes(attribute.typ),
    sDefault -> AttributeValueFormat.writes(attribute.default),
    sConstant -> attribute.constant,
    sSingleAssignment -> attribute.singleAssignment,
    sExpression -> attribute.expression,
    sOrdered -> attribute.ordered,
    sTransient -> attribute.transient
  )

  override def reads(json: JsValue): JsResult[MAttribute] = {
    for {
      name <- (json \ sName).validate[String]
      globalUnique <- (json \ sGlobalUnique).validate[Boolean]
      localUnique <- (json \ sLocalUnique).validate[Boolean]
      typ <- (json \ sType).validate(AttributeTypeFormat)
      default <- (json \ sDefault).validate(AttributeValueFormat)
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

}
