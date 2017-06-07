package de.htwg.zeta.server.model.modelValidator.validator

import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.Node

case class ModelValidationResultContainer(results: Seq[ModelValidationResult]) {

  def validResults: ModelValidationResultContainer = ModelValidationResultContainer(results.filter(_.valid))

  def invalidResults: ModelValidationResultContainer = ModelValidationResultContainer(results.filterNot(_.valid))

  def mkString: String = if (invalidResults.results.isEmpty) "Model instance is valid." else generateInvalidResultsString

  private def generateInvalidResultsString: String =
    s"""Model instance is invalid:
      |
      |${generateList.mkString("* ", "\n\n* ", "")}
    """.stripMargin

  private def generateList: Seq[String] = invalidResults.results.map { res =>
    val sb = new StringBuilder
    sb.append("Rule \"" + res.rule.name + "\" failed")

    res.modelElement match {
      case Some(el) => el match {
        case edge: Edge => sb.append(" for edge of type \"" + edge.`type`.name + "\" (edge-id: " + edge.id + ")")
        case node: Node => sb.append(" for node of type \"" + node.`type`.name + "\" (node-id: " + node.id + ")")
        case _ =>
      }
      case None =>
    }

    sb.append(".\n")
    sb.append("\tdescription: \"" + res.rule.description + "\"\n")
    sb.append("\tpossible fix: \"" + res.rule.possibleFix + "\"")

    sb.toString
  }

}
