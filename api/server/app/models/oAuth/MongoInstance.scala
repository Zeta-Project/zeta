package models.oAuth

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers
import com.novus.salat._
import org.joda.time.DateTime
import play.api.Play
import play.api.libs.json.Json

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
    MongoInstance("models").drop()

    // the following email addresses must exist in the secure social user db
    val adminAcc = "admin@htwg-konstanz.de"
    val exampleAcc = "example@htwg-konstanz.de"

    // setting up three clients, admin user owns credentials client, example user owns auth client and password client
    // "owns" => "was created by". Has nothing to do with usage later on, anyone can use any client
    val client1 = OAuthClient(new ObjectId, adminAcc, "client_credentials", "credClientid", "credClientSecret", None, new DateTime())
    val client2 = OAuthClient(new ObjectId, exampleAcc, "authorization_code", "authClientId", "authClientSecret", Some("http://localhost:3000/callback"), new DateTime())
    val client3 = OAuthClient(new ObjectId, exampleAcc, "password", "pwClientId", "pwClientSecret", None, new DateTime())

    // an additional client for testing OAuthLocalController
    val modigenClient = OAuthClient(new ObjectId, adminAcc, "implicit", "modigen-browser-app1", "", None, new DateTime())

    // auth code for authorization_code mode, can be used by example acc only
    val authCode = OAuthAuthorizationCode(
      new ObjectId,
      exampleAcc,
      client2.id,
      "authcode123",
      Some("http://localhost:3000/callback"),
      new DateTime()
    )

    // do inserts...
    OAuthClient.insert(client1)
    OAuthClient.insert(client2)
    OAuthClient.insert(client3)
    OAuthClient.insert(modigenClient)
    OAuthAuthorizationCode.insert(authCode)
  }
}