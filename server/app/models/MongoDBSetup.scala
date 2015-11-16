package models

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers
import com.novus.salat._
import org.joda.time.DateTime
import play.api.Play


object MongoDBSetup {
  //val mongoDB = MongoConnection()("moath")
}

package object custom_context {

  implicit val ctx = new Context {
    val name = "Custom Context"
  }
  ctx.registerClassLoader(Play.classloader(Play.current))

}

object Setup {

  def resetMongo(): Unit = {

    RegisterJodaTimeConversionHelpers()

    val client: MongoClient = MongoClient(MongoClientURI("mongodb://localhost:27017"))

    client("modigen_v3")("account").drop()
    client("modigen_v3")("oauth_accesstoken").drop()
    client("modigen_v3")("oauth_authcode").drop()
    client("modigen_v3")("oauth_client").drop()

    val bobAcc = Account(new ObjectId, "bob@example.com", "48181acd22b3edaebc8a447868a7df7ce629920a", new DateTime())  // pw: bob
    val aliceAcc = Account(new ObjectId, "alice@example.com", "522b276a356bdf39013dfabea2cd43e141ecc9e8", new DateTime()) // pw: alice

    val client1 = OauthClient(new ObjectId, bobAcc.id, "client_credentials", "bob_client_id", "bob_client_secret", None, new DateTime())
    val client2 = OauthClient(new ObjectId, aliceAcc.id, "authorization_code", "alice_client_id", "alice_client_secret", Some("http://localhost:3000/callback"), new DateTime())
    val client3 = OauthClient(new ObjectId, aliceAcc.id, "password", "alice_client_id2", "alice_client_secret2", None, new DateTime())

    val authCode = OauthAuthorizationCode(
      new ObjectId,
      aliceAcc.id,
      client2.id,
      "bob_code",
      Some("http://localhost:3000/callback"),
      new DateTime()
    )

    Account.insert(bobAcc)
    Account.insert(aliceAcc)
    OauthClient.insert(client1)
    OauthClient.insert(client2)
    OauthClient.insert(client3)
    OauthAuthorizationCode.insert(authCode)

  }
}

