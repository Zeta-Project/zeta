package models

import java.util.UUID

import com.mohiva.play.silhouette.api.Identity
import com.mohiva.play.silhouette.api.LoginInfo
import org.apache.commons.codec.binary.Hex
import play.api.libs.json.Json

/**
 * The user object.
 *
 * @param id        The unique ID of the user.
 * @param loginInfo The linked login info.
 * @param firstName The first name of the authenticated user.
 * @param lastName  The last name of the authenticated user.
 * @param email     The email of the authenticated provider.
 * @param activated Indicates that the user has activated its registration.
 */
case class User(
    id: UUID,
    loginInfo: LoginInfo,
    firstName: String,
    lastName: String,
    email: String,
    activated: Boolean
) extends Identity with Identifiable {

  /** The full name of the user. */
  lazy val fullName = s"$firstName $lastName"

}

object User {

  /**
   * The user id will be used in different parts of the application to identify an user.
   * In some parts of the application we have a limited char set for usage, e.g. for the name of an actor.
   * To avoid using not allowed characters in e.g. an actor name we encode the user id (users email) to hex.
   */
  def getUserId(loginInfo: LoginInfo): String = {
    new String(Hex.encodeHex(loginInfo.providerKey.getBytes))
  }

  implicit val jsonFormat = Json.format[User]
}
