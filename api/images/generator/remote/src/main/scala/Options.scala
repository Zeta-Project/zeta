import java.util.UUID

import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes

/**
 * Options which will be passed as parameters to this application
 * to create an generator template
 *
 * @param name The name of the generator which will be created
 */
case class CreateOptions(name: String)

object CreateOptions {
  implicit lazy val reads: Reads[CreateOptions] = Json.reads[CreateOptions]
}

/**
 * Options which will be passed to a remote generator call
 */
case class RemoteOptions(nodeType: String, modelId: UUID)

object RemoteOptions {
  implicit lazy val reads: Reads[RemoteOptions] = Json.reads[RemoteOptions]
  implicit lazy val write: Writes[RemoteOptions] = Json.writes[RemoteOptions]
}

/**
 * Result which will be emitted by the remote called generator
 */
case class RemoteResult(from: String, number: Int)

object RemoteResult {
  implicit lazy val reads: Reads[RemoteResult] = Json.reads[RemoteResult]
  implicit lazy val write: Writes[RemoteResult] = Json.writes[RemoteResult]
}

