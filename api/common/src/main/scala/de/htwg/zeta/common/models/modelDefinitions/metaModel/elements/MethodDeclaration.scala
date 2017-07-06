package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import scala.collection.immutable.Seq

import play.api.libs.json.Format
import play.api.libs.json.Json

case class MethodDeclaration(
    name: String,
    parameters: Seq[MethodParameter]
)

object MethodDeclaration {

  implicit val playJsonFormat: Format[MethodDeclaration] = Json.format[MethodDeclaration]

}
