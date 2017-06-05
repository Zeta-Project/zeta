package models.file

import java.util.UUID

import models.Entity
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes

case class File(id: UUID, name: String, content: String) extends Entity

object File {
  implicit lazy val reads: Reads[File] = Json.reads[File]
  implicit lazy val write: Writes[File] = Json.writes[File]
}
