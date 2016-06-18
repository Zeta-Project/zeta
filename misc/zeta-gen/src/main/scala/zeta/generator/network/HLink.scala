package zeta.generator.network

import play.api.libs.json.Json

case class HLink(rel: String, href: String, method: String)
object HLink {
  implicit val reads = Json.reads[HLink]
}
