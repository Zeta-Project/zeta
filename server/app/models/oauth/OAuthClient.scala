package models.oauth

import com.mongodb.casbah.Imports._
import com.novus.salat.annotations._
import com.novus.salat.dao.{ModelCompanion, SalatDAO}
import models.{MongoDbUserService, SecureSocialUser}
import org.joda.time.DateTime
import models.oauth.custom_context._


case class OauthClient(
  @Key("_id") id: ObjectId,
  ownerId: String,
  grantType: String,
  clientId: String,
  clientSecret: String,
  redirectUri: Option[String],
  createdAt: DateTime
)

object OauthClient extends ModelCompanion[OauthClient, ObjectId] {

  val collection =  MongoInstance("oauth_client")
  override val dao = new SalatDAO[OauthClient, ObjectId](collection = collection) {}

  def validate(clientId: String, clientSecret: String, grantType: String): Boolean = {
    val client = findOne(MongoDBObject("clientId" -> clientId, "clientSecret" -> clientSecret))
    client.map(c => c.grantType == grantType || grantType == "refresh_token").getOrElse(false)
  }

  def findByClientId(clientId: String): Option[OauthClient] = findOne(MongoDBObject("clientId" -> clientId))

  def findClientCredentials(clientId: String, clientSecret: String): Option[SecureSocialUser] = {
    val client = findOne(MongoDBObject("clientId" -> clientId, "clientSecret" -> clientSecret))
    client.map(c => MongoDbUserService.findOneById(c.ownerId)).getOrElse(None)
  }

}

