package zeta.generator.network

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class AccessToken(value: String, refreshToken: String, expiresIn: Int)
object AccessToken {
  implicit val tokenReads: Reads[AccessToken] = (
    (__ \ "access_token").read[String] and
      (__ \ "refresh_token").read[String] and
      (__ \ "expires_in").read[Int]
    )(AccessToken.apply _)
}