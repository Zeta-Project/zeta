package de.htwg.zeta.persistence.authInfo

import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsObject

case class ZetaLoginInfo(providerID: String, providerKey: String)

object ZetaLoginInfo extends Format[ZetaLoginInfo] {
  private val sProviderID = "providerID"
  private val sProviderKey = "providerKey"

  override def reads(json: JsValue): JsResult[ZetaLoginInfo] = for {
    providerID <- json.\(sProviderID).validate[String]
    providerKey <- json.\(sProviderKey).validate[String]

  } yield {
    ZetaLoginInfo(providerID, providerKey)
  }

  override def writes(o: ZetaLoginInfo): JsObject = Json.obj(
    sProviderID -> o.providerID,
    sProviderKey -> o.providerKey
  )
}
