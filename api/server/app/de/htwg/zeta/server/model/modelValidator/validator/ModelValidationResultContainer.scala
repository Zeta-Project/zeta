package de.htwg.zeta.server.model.modelValidator.validator

import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.Node
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsNull
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Writes

class ModelValidationResultContainer(val results: Seq[ModelValidationResult]) {

  def invalidResults: Seq[ModelValidationResult] = results.filterNot(_.valid)

}

object ModelValidationResultContainer {
  implicit val modelValidationResultContainerWrites: Writes[ModelValidationResultContainer] = new Writes[ModelValidationResultContainer] {
    override def writes(o: ModelValidationResultContainer): JsValue = Json.toJson(o.invalidResults)
  }

  implicit val modelValidationResultWrites: Writes[ModelValidationResult] = new Writes[ModelValidationResult] {
    override def writes(o: ModelValidationResult): JsValue = {

      val elementType = o.modelElement match {
        case Some(_: Node) => JsString("node")
        case Some(_: Edge) => JsString("edge")
        case _ => JsNull
      }

      val elementTypeName = o.modelElement match {
        case Some(node: Node) => JsString(node.`type`.name)
        case Some(edge: Edge) => JsString(edge.`type`.name)
        case _ => JsNull
      }

      val element = o.modelElement match {
        case Some(el) => Json.obj(
          "id" -> JsString(el.id),
          "type" -> elementType,
          "typeName" -> elementTypeName
        )
      }

      val valid = JsBoolean(o.valid)

      val rule = Json.obj(
        "name" -> JsString(o.rule.name),
        "description" -> JsString(o.rule.description),
        "possibleFix" -> JsString(o.rule.possibleFix)
      )

      Json.obj(
        "element" -> element,
        "valid" -> valid,
        "rule" -> rule
      )
    }
  }
}
