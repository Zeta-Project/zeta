package models.modelDefinitions.helper

import play.api.libs.json.Json

/**
 * Represents a HATEOS Link that can be serialized into JSON when used via REST
 * @param rel  the link type
 * @param href the uri to query
 * @param method the http method to use
 */
case class HLink(rel: String, href: String, method: String)

object HLink {
  implicit val reads = Json.reads[HLink]
  implicit val writes = Json.writes[HLink]
  // Helper methods for construction
  def get(rel: String, href: String) = HLink(rel, href, "GET")
  def post(rel: String, href: String) = HLink(rel, href, "POST")
  def put(rel: String, href: String) = HLink(rel, href, "PUT")
  def delete(rel: String, href: String) = HLink(rel, href, "DELETE")
}
