package models.modelDefinitions.helper

import play.api.libs.json.Json


case class HLink(rel: String, href: String, method: String)
object HLink {
  implicit val reads = Json.reads[HLink]
  implicit val writes = Json.writes[HLink]

  def get(rel: String, href: String) = HLink(rel, href, "GET")
  def post(rel: String, href: String) = HLink(rel, href, "POST")
  def put(rel: String, href: String) = HLink(rel, href, "PUT")
  def delete(rel: String, href: String) = HLink(rel, href, "DELETE")
}