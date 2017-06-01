package models

import java.util.UUID

import com.mohiva.play.silhouette.api.Identity
import play.api.libs.json.Json
import play.api.libs.json.OFormat

/**
 * The user object.
 *
 * @param id        The unique ID of the user.
 * @param firstName The first name of the authenticated user.
 * @param lastName  The last name of the authenticated user.
 * @param email     The email of the authenticated provider.
 * @param activated Indicates that the user has activated its registration.
 */
case class User(
    id: UUID,
    firstName: String,
    lastName: String,
    email: String,
    activated: Boolean
) extends Identity with Identifiable {

  /** The full name of the user. */
  val fullName = s"$firstName $lastName"

}

object User {

  /**
   * Play-Json conversion format.
   */
  implicit val jsonFormat: OFormat[User] = Json.format[User]

}
