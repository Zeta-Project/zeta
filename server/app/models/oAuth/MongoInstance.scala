package models.oAuth

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers
import com.novus.salat._
import models.model.ModelData
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

    // insert sample json model to be able to test model rest api
    val json = Json.parse(
      """
        |[
        |        {
        |            "id" : "846bc8a2-00fc-401f-b626-0b0252516aee",
        |            "mObj" : "mClass",
        |            "type" : "Male",
        |            "outputs" : {
        |                "isFather" : [
        |                    "8e9b1093-a589-4ae4-8e1e-1b3d63a3f842"
        |                ],
        |                "isHusband" : [
        |                    "ee204744-6322-49d4-928e-1442e8bc70c4"
        |                ]
        |            },
        |            "inputs" : {
        |                "isWife" : [
        |                    "666d4de7-e0f2-4620-8c19-d5469b40be1f"
        |                ]
        |            },
        |            "mAttributes" : {
        |                "FirstName" : [
        |                    "Hans"
        |                ],
        |                "Steuernummer" : [
        |                    "12"
        |                ],
        |                "Geburtstag" : [
        |                    "12-02-2015"
        |                ]
        |            }
        |        },
        |        {
        |            "id" : "a264a43b-6f97-4257-9243-baddbf745490",
        |            "mObj" : "mClass",
        |            "type" : "Female",
        |            "outputs" : {
        |                "isMother" : [
        |                    "d5b00503-5378-4df3-9e27-4d2b0d018750"
        |                ],
        |                "isWife" : [
        |                    "666d4de7-e0f2-4620-8c19-d5469b40be1f"
        |                ]
        |            },
        |            "inputs" : {
        |                "isHusband" : [
        |                    "ee204744-6322-49d4-928e-1442e8bc70c4"
        |                ]
        |            },
        |            "mAttributes" : {
        |                "FirstName" : [
        |                    "Magda"
        |                ],
        |                "Steuernummer" : [
        |                    "13"
        |                ],
        |                "Geburtstag" : [
        |                    "13-02-2015"
        |                ]
        |            }
        |        },
        |        {
        |            "id" : "1c2861fe-bbca-4842-9436-4d1b9d9a4d05",
        |            "mObj" : "mClass",
        |            "type" : "Male",
        |            "outputs" : [],
        |            "inputs" : {
        |                "isFather" : [
        |                    "8e9b1093-a589-4ae4-8e1e-1b3d63a3f842"
        |                ],
        |                "isMother" : [
        |                    "d5b00503-5378-4df3-9e27-4d2b0d018750"
        |                ]
        |            },
        |            "mAttributes" : {
        |                "FirstName" : [
        |                    "Kevin"
        |                ],
        |                "Steuernummer" : [
        |                    "14"
        |                ],
        |                "Geburtstag" : [
        |                    "14-02-2015"
        |                ]
        |            }
        |        },
        |        {
        |            "id" : "8e9b1093-a589-4ae4-8e1e-1b3d63a3f842",
        |            "mObj" : "mRef",
        |            "type" : "isFather",
        |            "source" : {
        |                "Male" : [
        |                    "846bc8a2-00fc-401f-b626-0b0252516aee"
        |                ]
        |            },
        |            "target" : {
        |                "Male" : [
        |                    "1c2861fe-bbca-4842-9436-4d1b9d9a4d05"
        |                ]
        |            }
        |        },
        |        {
        |            "id" : "ee204744-6322-49d4-928e-1442e8bc70c4",
        |            "mObj" : "mRef",
        |            "type" : "isHusband",
        |            "source" : {
        |                "Male" : [
        |                    "846bc8a2-00fc-401f-b626-0b0252516aee"
        |                ]
        |            },
        |            "target" : {
        |                "Female" : [
        |                    "a264a43b-6f97-4257-9243-baddbf745490"
        |                ]
        |            }
        |        },
        |        {
        |            "id" : "d5b00503-5378-4df3-9e27-4d2b0d018750",
        |            "mObj" : "mRef",
        |            "type" : "isMother",
        |            "source" : {
        |                "Female" : [
        |                    "a264a43b-6f97-4257-9243-baddbf745490"
        |                ]
        |            },
        |            "target" : {
        |                "Male" : [
        |                    "846bc8a2-00fc-401f-b626-0b0252516aee"
        |                ]
        |            }
        |        },
        |        {
        |            "id" : "666d4de7-e0f2-4620-8c19-d5469b40be1f",
        |            "mObj" : "mRef",
        |            "type" : "isWife",
        |            "source" : {
        |                "Female" : [
        |                    "a264a43b-6f97-4257-9243-baddbf745490"
        |                ]
        |            },
        |            "target" : {
        |                "Male" : [
        |                    "846bc8a2-00fc-401f-b626-0b0252516aee"
        |                ]
        |            }
        |        }
        |    ]
      """.stripMargin)

    val model = ModelData(new ObjectId("564e048cdb5c98e56b7deba5"), Some(exampleAcc), Some(new DateTime()), json)
    ModelData.save(model)

  }
}