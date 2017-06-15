package de.htwg.zeta.common.models.entity

import java.util.UUID

import play.api.libs.json.Format
import play.api.libs.json.Json

case class File(
    id: UUID,
    name: String,
    content: String
) extends Entity

object File {

  implicit lazy val playJsonFormat: Format[File] = Json.format[File]

}
