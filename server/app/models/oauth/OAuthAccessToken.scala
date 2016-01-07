package models.oAuth

import java.security.SecureRandom

import com.mongodb.casbah.Imports._
import com.novus.salat.annotations._
import com.novus.salat.dao.{ModelCompanion, SalatDAO}
import models.SecureSocialUser
import org.joda.time.DateTime
import models.oAuth.custom_context._

import scala.util.Random

case class OAuthAccessToken(
                             @Key("_id") id: ObjectId,
                             accountId: String,
                             oauthClientId: ObjectId,
                             accessToken: String,
                             refreshToken: String,
                             createdAt: DateTime
                           )

object OAuthAccessToken extends ModelCompanion[OAuthAccessToken, ObjectId] {

  val collection = MongoInstance("oauth_accesstoken")
  override val dao = new SalatDAO[OAuthAccessToken, ObjectId](collection = collection) {}

  private def randomString(length: Int) = new Random(new SecureRandom()).alphanumeric.take(length).mkString

  def create(user: SecureSocialUser, client: OAuthClient): OAuthAccessToken = {
    val token = OAuthAccessToken(
      new ObjectId,
      user.profile.userId,
      client.id,
      randomString(40),
      randomString(40),
      new DateTime()
    )
    insert(token)
    token
  }

  // sql version returns Int at this point
  def delete(user: SecureSocialUser, client: OAuthClient): Unit = {
    remove(MongoDBObject("accountId" -> user.profile.userId, "oauthClientId" -> client.id))
  }

  def refresh(user: SecureSocialUser, client: OAuthClient): OAuthAccessToken = {
    delete(user, client)
    create(user, client)
  }

  def findByAccessToken(accessToken: String): Option[OAuthAccessToken] = {
    findOne(MongoDBObject("accessToken" -> accessToken))
  }

  def findByAuthorized(user: SecureSocialUser, clientId: String): Option[OAuthAccessToken] = {
    val client = OAuthClient.findByClientId(clientId)
    client.map(c => findOne(MongoDBObject("accountId" -> user.profile.userId, "oauthClientId" -> c.id))).getOrElse(None)
  }

  def findByRefreshToken(refreshToken: String): Option[OAuthAccessToken] = {
    val expireAt = new DateTime().minusMonths(1)
    findOne(MongoDBObject("refreshToken" -> refreshToken, "createdAt" -> MongoDBObject("$gt" -> expireAt)))
  }

}
