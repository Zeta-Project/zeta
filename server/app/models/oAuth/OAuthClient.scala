package models.oAuth

import com.mongodb.casbah.Imports._
import com.novus.salat.annotations._
import com.novus.salat.dao.{ModelCompanion, SalatDAO}
import models.{MongoDbUserService, SecureSocialUser}
import org.joda.time.DateTime
import models.oAuth.custom_context._


case class OAuthClient(
                        @Key("_id") id: ObjectId,
                        ownerId: String,
                        grantType: String,
                        clientId: String,
                        clientSecret: String,
                        redirectUri: Option[String],
                        createdAt: DateTime
                      )

object OAuthClient extends ModelCompanion[OAuthClient, ObjectId] {

  lazy val userService = new MongoDbUserService()

  val collection = MongoInstance("oauth_client")
  override val dao = new SalatDAO[OAuthClient, ObjectId](collection = collection) {}

  def validate(clientId: String, clientSecret: String, grantType: String): Boolean = {
    val client = findOne(MongoDBObject("clientId" -> clientId, "clientSecret" -> clientSecret))
    client.map(c => c.grantType == grantType || grantType == "refresh_token").getOrElse(false)
  }

  def findByClientId(clientId: String): Option[OAuthClient] = findOne(MongoDBObject("clientId" -> clientId))

  def findClientCredentials(clientId: String, clientSecret: String): Option[SecureSocialUser] = {
    val client = findOne(MongoDBObject("clientId" -> clientId, "clientSecret" -> clientSecret))
    client.map(c => userService.findOneById(c.ownerId)).getOrElse(None)
  }

}
