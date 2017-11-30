package de.htwg.zeta.common.format.entity

import java.util.UUID

import de.htwg.zeta.common.models.entity.Generator
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class GeneratorFormat(
    sId: String = "id",
    sName: String = "name",
    sImageId: String = "imageId",
    sFiles: String = "files",
    sDeleted: String = "deleted"
) extends OFormat[Generator] {

  override def writes(o: Generator): JsObject = Json.obj(
    sId -> o.id,
    sName -> o.name,
    sImageId -> o.imageId,
    sFiles -> Writes.map[String].writes(o.files.map(e => (e._2.toString, e._2))),
    sDeleted -> o.deleted
  )

  override def reads(json: JsValue): JsResult[Generator] = for {
    id <- (json \ sId).validate[UUID]
    name <- (json \ sName).validate[String]
    imageId <- (json \ sImageId).validate[UUID]
    files <- (json \ sFiles).validate(Reads.map[String])
    deleted <- (json \ sDeleted).validateOpt[Boolean]
  } yield {
    Generator(id, name, imageId, files.map(e => (UUID.fromString(e._1), e._2)), deleted.getOrElse(false))
  }

}
