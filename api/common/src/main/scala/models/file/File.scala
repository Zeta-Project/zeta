package models.file

import play.api.libs.json.{ Json, Reads, Writes }

case class File(name: String, content: String)

object File {
  implicit lazy val reads: Reads[File] = Json.reads[File]
  implicit lazy val write: Writes[File] = Json.writes[File]
}
