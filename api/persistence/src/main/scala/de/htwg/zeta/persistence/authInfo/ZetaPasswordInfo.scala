package de.htwg.zeta.persistence.authInfo

import play.api.libs.json.OFormat
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult

case class ZetaPasswordInfo(
    hasher: String,
    password: String,
    salt: Option[String] = None)

object ZetaPasswordInfo extends OFormat[ZetaPasswordInfo] {

  private val hasherLiteral = "hasher"
  private val passwordLiteral = "password"
  private val saltLiteral = "salt"

  override def reads(json: JsValue): JsResult[ZetaPasswordInfo] = for {
    hasher <- json.\(hasherLiteral).validate[String]
    password <- json.\(passwordLiteral).validate[String]
    salt <- json.\(saltLiteral).validateOpt[String]
  } yield {
    ZetaPasswordInfo(hasher, password, salt)
  }

  override def writes(o: ZetaPasswordInfo): JsObject = Json.obj(
    hasherLiteral -> o.hasher,
    passwordLiteral -> o.password,
    saltLiteral -> o.salt
  )
}
