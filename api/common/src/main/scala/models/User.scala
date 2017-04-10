package models

import java.util.UUID

import com.mohiva.play.silhouette.api.Identity
import com.mohiva.play.silhouette.api.LoginInfo
import org.apache.commons.codec.binary.Hex
import play.api.libs.json.Json

/**
 * The user object.
 *
 * @param userID The unique ID of the user.
 * @param loginInfo The linked login info.
 * @param firstName Maybe the first name of the authenticated user.
 * @param lastName Maybe the last name of the authenticated user.
 * @param fullName Maybe the full name of the authenticated user.
 * @param email Maybe the email of the authenticated provider.
 * @param avatarURL Maybe the avatar URL of the authenticated provider.
 * @param activated Indicates that the user has activated its registration.
 */
case class User(
    userID: UUID,
    loginInfo: LoginInfo,
    firstName: Option[String],
    lastName: Option[String],
    fullName: Option[String],
    email: Option[String],
    avatarURL: Option[String],
    activated: Boolean
) extends Identity {

  /**
   * Tries to construct a name.
   *
   * @return Maybe a name.
   */
  def name = fullName.orElse {
    firstName -> lastName match {
      case (Some(f), Some(l)) => Some(f + " " + l)
      case (Some(f), None) => Some(f)
      case (None, Some(l)) => Some(l)
      case _ => None
    }
  }
}

object User {
  def getUserId(user: User): String = getUserId(user.loginInfo)

  /**
   * The user id will be used in different parts of the application to identify an user.
   * In some parts of the application we have a limited char set for usage, e.g. for the name of an actor.
   * To avoid using not allowed characters in e.g. an actor name we encode the user id (users email) to hex.
   */
  def getUserId(loginInfo: LoginInfo): String = new String(Hex.encodeHex(loginInfo.providerKey.getBytes))

  implicit val jsonFormat = Json.format[User]
}
