package de.htwg.zeta.persistence.microService

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import models.User
import models.document.PasswordInfoEntity
import models.document.UserEntity
import spray.json.DefaultJsonProtocol
import spray.json.JsString
import spray.json.JsValue
import spray.json.RootJsonFormat
import spray.json.deserializationError

/**
 * Provides implicit conversion for using the Spray-Json library.
 */
object PersistenceJsonProtocol extends DefaultJsonProtocol {

  private implicit object UuidJsonFormat extends RootJsonFormat[UUID] {

    /** Convert a UUID to a String.
     *
     * @param x UUID
     * @return String
     */
    def write(x: UUID): JsValue = {
      JsString(x.toString)
    }

    /** Convert a String to a UUID.
     *
     * @param value String
     * @return UUID
     */
    def read(value: JsValue): UUID = {
      value match {
        case JsString(x) => UUID.fromString(x)
        case x: Any => deserializationError(s"Expected UUID as JsString, but got $x")
      }
    }

  }

  private implicit val passwordInfo: RootJsonFormat[PasswordInfo] = jsonFormat3(PasswordInfo)
  private implicit val loginInfoFormat: RootJsonFormat[LoginInfo] = jsonFormat2(LoginInfo)
  private implicit val userFormat: RootJsonFormat[User] = jsonFormat8(User.apply)

  /** Spray-Json conversion protocol for [[models.document.UserEntity]] */
  implicit val passwordInfoEntity: RootJsonFormat[PasswordInfoEntity] = jsonFormat3(PasswordInfoEntity.apply)

  /** Spray-Json conversion protocol for [[models.document.UserEntity]] */
  implicit val userEntityFormat: RootJsonFormat[UserEntity] = jsonFormat3(UserEntity.apply)

}
