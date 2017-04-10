import play.api.libs.json.{ Json, Reads }

/**
 * Options which will be passed as parameters to this application
 * to create an generator template
 *
 * @param name The name of the generator which will be created
 */
case class CreateOptions(name: String, metaModelRelease: String)

object CreateOptions {
  implicit lazy val readChanges: Reads[CreateOptions] = Json.reads[CreateOptions]
}
