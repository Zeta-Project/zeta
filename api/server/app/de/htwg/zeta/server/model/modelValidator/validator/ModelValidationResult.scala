package de.htwg.zeta.server.model.modelValidator.validator

import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.server.model.modelValidator.validator.rules.Rule
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsNull
import play.api.libs.json.Json
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.Writes

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 *
 * The result of the validation of one rule against one model element.
 *
 * @param rule         The rule.
 * @param valid        Element is valid or invalid.
 * @param modelElement The modelElement.
 */
case class ModelValidationResult(rule: Rule, valid: Boolean, modelElement: Option[Either[Node, Edge]] = None)

object ModelValidationResult {

  /**
   * Implicit converter from ModelValidationResult to its JSON representation, so the following calls succeed:
   *
   * {{{
   *   Json.toJson(modelValidationResult)
   *
   *   Json.toJson(Seq(modelValidationResult, ...))
   * }}}
   */
  implicit val modelValidationResultWrites: Writes[ModelValidationResult] = new Writes[ModelValidationResult] {
    override def writes(o: ModelValidationResult): JsValue = {
      val element = o.modelElement match {
        case Some(Left(node: Node)) => Json.obj(
          "id" -> JsString(node.name),
          "type" -> JsString("node"),
          "typeName" -> JsString(node.className)
        )
        case Some(Right(edge: Edge)) => Json.obj(
          "id" -> JsString(edge.name),
          "type" -> JsString("edge"),
          "typeName" -> JsString(edge.referenceName)
        )
        case None => JsNull
      }

      Json.obj(
        "element" -> element,
        "rule" -> Json.obj(
          "name" -> JsString(o.rule.name),
          "description" -> JsString(o.rule.description),
          "possibleFix" -> JsString(o.rule.possibleFix)
        ),
        "valid" -> JsBoolean(o.valid)
      )
    }
  }
}

