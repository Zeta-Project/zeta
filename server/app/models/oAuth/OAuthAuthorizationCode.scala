package models.oAuth

import com.mongodb.casbah.Imports._
import com.novus.salat.annotations._
import com.novus.salat.dao.{ModelCompanion, SalatDAO}
import org.joda.time.DateTime
import models.oAuth.custom_context._

case class OauthAuthorizationCode(
                                   @Key("_id") id: ObjectId,
                                   accountId: String,
                                   oauthClientId: ObjectId,
                                   code: String,
                                   redirectUri: Option[String],
                                   createdAt: DateTime
                                 )

object OauthAuthorizationCode extends ModelCompanion[OauthAuthorizationCode, ObjectId] {

  val collection = MongoInstance("oauth_authcode")
  override val dao = new SalatDAO[OauthAuthorizationCode, ObjectId](collection = collection) {}

  def delete(code: String): Unit = remove(MongoDBObject("code" -> code))

  def findByCode(code: String): Option[OauthAuthorizationCode] = {
    val expireAt = new DateTime().minusMinutes(30)
    findOne(MongoDBObject("code" -> code, "createdAt" -> MongoDBObject("$gt" -> expireAt)))
  }

}
