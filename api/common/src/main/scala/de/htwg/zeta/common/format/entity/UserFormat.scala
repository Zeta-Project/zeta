package de.htwg.zeta.common.format.entity

import java.util.UUID

import de.htwg.zeta.common.models.entity.User
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat

object UserFormat extends OFormat[User] {

  val sId = "id"
  val sFirstName = "firstName"
  val sLastName = "lastName"
  val sEmail = "email"
  val sActivated = "activated"

  override def writes(user: User): JsObject = Json.obj(
    sId -> user.id,
    sFirstName -> user.firstName,
    sLastName -> user.lastName,
    sEmail -> user.email,
    sActivated -> user.activated
  )

  override def reads(json: JsValue): JsResult[User] = for {
    id <- (json \ sId).validate[UUID]
    firstName <- (json \ sFirstName).validate[String]
    lastName <- (json \ sLastName).validate[String]
    email <- (json \ sEmail).validate[String]
    activated <- (json \ sActivated).validate[Boolean]
  } yield {
    User(id, firstName, lastName, email, activated)
  }

}
