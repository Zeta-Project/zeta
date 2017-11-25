package de.htwg.zeta.common.format

import java.util.UUID

import de.htwg.zeta.common.models.entity.Generator
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Writes

/**
 * @author Philipp Daniels
 */
object GeneratorFormat extends Writes[Generator] {

  override def writes(o: Generator): JsValue = {
    Json.obj(
      "id" -> o.id.toString,
      "name" -> o.name,
      "imageId" -> o.imageId,
      "files" -> createFiles(o.files)
    )
  }

  private def createFiles(files: Map[UUID, String]): JsObject = {
    val objects = files.map{
      case (key, value) => Json.obj(key.toString -> value)
    }
    objects.reduceLeft((a, b) => a ++ b)
  }
}
