package de.htwg.zeta.persistence.authInfo

import com.mohiva.play.silhouette.api.util.PasswordInfo
import play.api.libs.json.OFormat
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult

case class ZetaPasswordInfo(
    hasher: String,
    password: String,
    salt: Option[String] = None) {

  def toPasswordInfo(): PasswordInfo = PasswordInfo(hasher, password, salt)
}

object ZetaPasswordInfo extends OFormat[ZetaPasswordInfo] {

  def apply(passwordInfo: PasswordInfo): ZetaPasswordInfo = ZetaPasswordInfo(passwordInfo.hasher, passwordInfo.password, passwordInfo.salt)

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
