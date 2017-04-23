package models.frontend

import julienrf.json.derived
import play.api.libs.json.__
import play.api.libs.json.OFormat

sealed trait GeneratorRequest extends Request
case class RunGeneratorFromGenerator(parent: String, key: String, generator: String, options: String) extends GeneratorRequest
case class ToGenerator(index: Int, key: String, receiver: String, message: String) extends GeneratorRequest

object GeneratorRequest {
  implicit lazy val format: OFormat[GeneratorRequest] = derived.flat.oformat((__ \ "action").format[String])
}

sealed trait GeneratorResponse extends Response
case class StartGeneratorError(key: String, reason: String) extends GeneratorResponse
case class FromGenerator(index: Int, key: String, message: String) extends GeneratorResponse
case class GeneratorCompleted(key: String, result: Int) extends GeneratorResponse

object FromGenerator {
  implicit val formatFromGenerator: OFormat[FromGenerator] = derived.oformat
}

object GeneratorResponse {
  implicit lazy val format: OFormat[GeneratorResponse] = derived.flat.oformat((__ \ "type").format[String])
}
