package models

import java.security.SecureRandom

import com.mongodb.casbah.Imports._
import com.novus.salat.annotations._
import com.novus.salat.dao.{ModelCompanion, SalatDAO}
import org.joda.time.DateTime
import models.custom_context._

import scala.util.Random

case class OauthAccessToken(
  @Key("_id") id: ObjectId,
  accountId: String,
  oauthClientId: ObjectId,
  accessToken: String,
  refreshToken: String,
  createdAt: DateTime
)

object OauthAccessToken extends ModelCompanion[OauthAccessToken, ObjectId] {

  val client: MongoClient = MongoClient(MongoClientURI("mongodb://localhost:27017"))
  val collection = client("modigen_v3")("oauth_accesstoken")
  override val dao = new SalatDAO[OauthAccessToken, ObjectId](collection = collection) {}

  private def randomString(length: Int) = new Random(new SecureRandom()).alphanumeric.take(length).mkString

  def create(account: Account, client: OauthClient): OauthAccessToken = {
    val token = OauthAccessToken(
      new ObjectId,
      account.id,
      client.id,
      randomString(40),
      randomString(40),
      new DateTime()
    )
    insert(token)
    token
  }

  // sql version returns Int at this point
  def delete(account: Account, client: OauthClient): Unit = {
    remove(MongoDBObject("accountId" -> account.id, "oauthClientId" -> client.id))
  }

  def refresh(account: Account, client: OauthClient): OauthAccessToken = {
    delete(account, client)
    create(account, client)
  }

  def findByAccessToken(accessToken: String): Option[OauthAccessToken] = {
    findOne(MongoDBObject("accessToken" -> accessToken))
  }

  def findByAuthorized(account: Account, clientId: String): Option[OauthAccessToken] = {
    val client = OauthClient.findByClientId(clientId)
    client.map(c => findOne(MongoDBObject("accountId" -> account.id, "oauthClientId" -> c.id))).getOrElse(None)
  }

  def findByRefreshToken(refreshToken: String): Option[OauthAccessToken] = {
    val expireAt = new DateTime().minusMonths(1)
    findOne(MongoDBObject("refreshToken" -> refreshToken, "createdAt" -> MongoDBObject("$gt" -> expireAt)))
  }

}
