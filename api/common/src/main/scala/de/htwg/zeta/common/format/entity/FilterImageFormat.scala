package de.htwg.zeta.common.format.entity

import java.util.UUID

import de.htwg.zeta.common.models.entity.FilterImage
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class FilterImageFormat(
    sId: String = "id",
    sName: String = "name",
    sDockerImage: String = "dockerImage"
) extends OFormat[FilterImage] {

  override def writes(image: FilterImage): JsObject = Json.obj(
    sId -> image.id,
    sName -> image.name,
    sDockerImage -> image.dockerImage
  )

  override def reads(json: JsValue): JsResult[FilterImage] = for {
    id <- (json \ sId).validate[UUID]
    name <- (json \ sName).validate[String]
    dockerImage <- (json \ sDockerImage).validate[String]
  } yield {
    FilterImage(id, name, dockerImage)
  }

}
