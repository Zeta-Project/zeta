package zeta.generator.domain.model

import play.api.libs.json.Json
import zeta.generator.network.HLink

case class ModelShortInfo(name: String, id: String, links: Seq[HLink])
object ModelShortInfo {
  implicit val reads = Json.reads[ModelShortInfo]
}
