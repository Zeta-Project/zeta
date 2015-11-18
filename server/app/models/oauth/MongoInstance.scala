package models.oauth

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers
import com.novus.salat._
import org.joda.time.DateTime
import play.api.Play

// Instantiates a single mongo client which manages its own connection pool -> should be reused
object MongoInstance {
  val mongoClient = MongoClient(new ServerAddress(Play.current.configuration.getString("mongodb.ip").get))
  val db = mongoClient(Play.current.configuration.getString("mongodb.name").get)

  def apply(coll: String) = db(coll)
}

// custom context used for salat
package object custom_context {
  implicit val ctx = new Context {
    val name = "salat_custom_context"
  }
  ctx.registerClassLoader(Play.classloader(Play.current))
}

// Sets up test data for oauth
object OAuthSetup {

  def resetMongo(): Unit = {

    RegisterJodaTimeConversionHelpers()

    MongoInstance("oauth_accesstoken").drop()
    MongoInstance("oauth_authcode").drop()
    MongoInstance("oauth_client").drop()

    // the following email addresses must exist in the secure social user db
    val adminAcc = "admin@htwg-konstanz.de"
    val exampleAcc = "example@htwg-konstanz.de"

    // setting up three clients, admin user uses credentials client, exmaple user uses auth client and passwort client
    val client1 = OauthClient(new ObjectId, adminAcc, "client_credentials", "credClientid", "credClientSecret", None, new DateTime())
    val client2 = OauthClient(new ObjectId, exampleAcc, "authorization_code", "authClientId", "authClientSecret", Some("http://localhost:3000/callback"), new DateTime())
    val client3 = OauthClient(new ObjectId, exampleAcc, "password", "pwClientId", "pwClientSecret", None, new DateTime())

    // auth code for auth client user
    val authCode = OauthAuthorizationCode(
      new ObjectId,
      exampleAcc,
      client2.id,
      "authcode123",
      Some("http://localhost:3000/callback"),
      new DateTime()
    )

    OauthClient.insert(client1)
    OauthClient.insert(client2)
    OauthClient.insert(client3)
    OauthAuthorizationCode.insert(authCode)

  }
}