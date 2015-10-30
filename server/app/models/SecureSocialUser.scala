package models

import _root_.java.security.SecureRandom
import _root_.java.util.UUID

import akka.actor.FSM.->
import argonaut.DecodeJson
import com.mongodb.ServerAddress
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat._
import org.joda.time.DateTime

import play.api.{Play, Logger}
import securesocial.core._
import securesocial.core.providers.MailToken
import securesocial.core.providers.utils.PasswordHasher
import securesocial.core.services.{SaveMode, UserService}

import scala.concurrent.Future
import scala.util.Random


/** Extend this to store custom info for users */
case class SecureSocialUser(uuid: UUID, admin: Boolean = false, profile: BasicProfile, oauthClients: List[OauthClient], createdAt: Long = System.currentTimeMillis / 1000)

case class OauthClient(grant_type: String, client_id: String, client_secret: String, redirect_uri: String = null, created_at: Long = System.currentTimeMillis, oauth_authorization_code: Oauth_authorization_code = null, oauth_access_token: Oauth_access_token = null)

case class Oauth_authorization_code(code: String, redirect_uri: String, created_at: Long = System.currentTimeMillis / 1000)

case class Oauth_access_token(access_token: String = null, refresh_token: String = null, created_at: Long = System.currentTimeMillis / 1000)


/** User Service Object implements SecureSocial Users */
object MongoDbUserService extends UserService[SecureSocialUser] {

  /** Salat Context **/
  implicit val ctx = new Context {
    val name = "Custom_Salat_Context"
  }
  ctx.registerClassLoader(Play.classloader(Play.current))


  import com.mongodb.casbah.commons.conversions.scala._

  RegisterJodaTimeConversionHelpers()

  val log = Logger(this getClass() getName())

  val mongoClient = MongoClient(new ServerAddress(Play.current.configuration.getString("mongodb.ip").get))
  val db = mongoClient(Play.current.configuration.getString("mongodb.name").get)
  val coll = db("Users")
  val tokenColl = db("Tokens")

  /** Create test users if userdb is empty */
  if (getNumberOfRegisteredUsers == 0) {

    val testUser = new BasicProfile(
      providerId = "userpass",
      userId = "example@htwg-konstanz.de",
      firstName = Some("Example"),
      lastName = Some("Example"),
      fullName = Some("Testuser"),
      email = Some("example@htwg-konstanz.de"),
      avatarUrl = None,
      authMethod = AuthenticationMethod.UserPassword,
      oAuth1Info = None,
      oAuth2Info = None,
      passwordInfo = Some(new PasswordHasher.Default().hash("supersecretpassword"))
    )

    val oauthClient = new OauthClient(
      grant_type = "client_credentials",
      client_id = "client_ID_BOB",
      client_secret = "Secret",
      redirect_uri = ""
    )

    val oauthCl = List[OauthClient](oauthClient)

    MongoDbUserService.save(profile = testUser, admin = false, mode = SaveMode.PasswordChange, oauthClientList = oauthCl)
    // admin@htwg-konstanz.de:admin
    val admin = new BasicProfile(
      providerId = "userpass",
      userId = "admin@htwg-konstanz.de",
      firstName = Some("Admin"),
      lastName = Some("Admin"),
      fullName = Some("Adminuser"),
      email = Some("admin@htwg-konstanz.de"),
      avatarUrl = None,
      authMethod = AuthenticationMethod.UserPassword,
      oAuth1Info = None,
      oAuth2Info = None,
      passwordInfo = Some(new PasswordHasher.Default().hash("supersecretpassword"))
    )
    MongoDbUserService.save(profile = admin, admin = true, mode = SaveMode.PasswordChange, oauthClientList = oauthCl)
  }


  def getNumberOfRegisteredUsers: Int = coll.count()

  override def find(providerId: String, userId: String): Future[Option[BasicProfile]] = {
    Future.successful(
      coll.findOne(MongoDBObject("profile.providerId" -> providerId, "profile.userId" -> userId)) match {
        case Some(obj) => Some(obj.profile)
        case None => None
      }
    )
  }

  override def findByEmailAndProvider(email: String, providerId: String): Future[Option[BasicProfile]] = {
    Future.successful(
      coll.findOne(MongoDBObject("profile" ->("profile.email" -> email, "profile.providerId" -> providerId))) match {
        case Some(obj) => Some(obj.profile)
        case None => None
      }
    )
  }

  override def deleteToken(uuid: String): Future[Option[MailToken]] = {
    Future.successful(
      tokenColl.findOne(MongoDBObject("uuid" -> uuid)) match {
        case Some(t) => Some(grater[MailToken].asObject(t))
        case _ => None
      }
    )
  }

  /** We du not support linking of profiles */
  override def link(current: SecureSocialUser, to: BasicProfile): Future[SecureSocialUser] = {
    Future.successful(current)
  }

  override def passwordInfoFor(user: SecureSocialUser): Future[Option[PasswordInfo]] = {
    Future.successful(
      coll.findOne(MongoDBObject("uuid" -> user.uuid.toString)) match {
        case Some(info) => info.profile.passwordInfo
        case None => None
      }
    )
  }

  override def findToken(token: String): Future[Option[MailToken]] = {
    Future.successful(
      tokenColl.findOne(MongoDBObject("uuid" -> token)) match {
        case Some(t) => Some(grater[MailToken].asObject(t))
        case _ => None
      }
    )
  }

  override def deleteExpiredTokens(): Unit = {
    log.debug("deleteExpiredTokens")
  }

  override def updatePasswordInfo(user: SecureSocialUser, info: PasswordInfo): Future[Option[BasicProfile]] = {
    Future.successful {
      val updated = new SecureSocialUser(user.uuid, user.admin, user.profile.copy(passwordInfo = Some(info)),oauthClients = user.oauthClients)
      coll.update(MongoDBObject("uuid" -> user.uuid.toString), updated, upsert = true)
      Option(updated.profile)
    }
  }

  override def saveToken(token: MailToken): Future[MailToken] = {
    tokenColl.save(grater[MailToken].asDBObject(token))
    Future.successful(token)
  }

  override def save(profile: BasicProfile, mode: SaveMode): Future[SecureSocialUser] = Future.successful(
    coll.findOne(MongoDBObject("profile.userId" -> profile.userId, "profile.providerId" -> profile.providerId)) match {
      case None =>
        log.debug(profile.toString)
        log.error("save mit bool")
        val created = new SecureSocialUser(UUID.randomUUID(), admin = false, profile = profile, oauthClients = null)
        coll.save(created)
        created

      case Some(obj) =>
        log.debug(profile.toString)
        val old = new MongoDBObject(obj)
        val updated = SecureSocialUser(old.uuid, old.admin, profile,old.oauthClients)
        coll.update(MongoDBObject("uuid" -> old.uuid), updated, upsert = false)
        updated
    }
  )

  def save(profile: BasicProfile, admin: Boolean, mode: SaveMode, oauthClientList: List[OauthClient]): Future[SecureSocialUser] = Future.successful(
    coll.findOne(MongoDBObject("profile.userId" -> profile.userId, "profile.providerId" -> profile.providerId)) match {
      case None =>
        log.debug(profile.toString)
        log.error("save mit bool")
        val created = new SecureSocialUser(UUID.randomUUID(), admin, profile, oauthClientList)
        coll.save(created)
        created

      case Some(obj) =>
        log.debug(profile.toString)
        val old = new MongoDBObject(obj)
        val updated = SecureSocialUser(old.uuid, admin, profile, oauthClientList)
        coll.update(MongoDBObject("uuid" -> old.uuid), updated, upsert = false)
        updated
    }
  )

  def makeAdmin(email: String): Boolean = setAdminStatus(email = email, admin = true)

  def revokeAdmin(email: String): Boolean = setAdminStatus(email = email, admin = false)

  def setAdminStatus(email: String, admin: Boolean): Boolean = {
    coll.findOne(MongoDBObject("profile.userId" -> email)) match {
      case Some(obj) =>
        val user = new MongoDBObject(obj)
        val update = $set("admin" -> admin)
        coll.update(user, update)
        true

      case None => false
    }
  }

  def createClient(user: SecureSocialUser,oauthclient: OauthClient ): Boolean = {

    //val oauthclient = new OauthClient(grant_type = "Bla", client_id = "Client_ID" , client_secret = "client Secret")

    coll.findOne(MongoDBObject("uuid" -> user.uuid)) match {
      case Some(obj) =>
        val old = new MongoDBObject(obj)
        val newlist = old.oauthClients.::(oauthclient)
        val update = SecureSocialUser(old.uuid, old.admin, old.profile, newlist)
        coll.update(MongoDBObject("uuid" -> old.uuid), update, upsert = false)
        true
      case None => false
    }
  }

  // OAuth Methodes AuthClient
  def validate(clientId: String, clientSecret: String, grantType: String): Boolean = {

    coll.findOne(MongoDBObject("oauthClients.client_id" -> clientId, "oauthClients.client_secret" -> clientSecret, "oauthClients.grant_type" -> grantType) ++("oauthClients.client_id" -> clientId, "oauthClients.client_secret" -> clientSecret, "oauthClients.grant_type" -> "refresh_token")) match {
      case Some(obj) => true
      case None => false
    }
  }

  def findByClientId(clientId: String): Option[OauthClient] = {

    coll.findOne(MongoDBObject("oauthClients.client_id" -> clientId))  match {
      case Some(obj) => Some(obj.oauthClients.head)
      case None => None
    }
  }

  def findClientCredentials(clientId: String, clientSecret: String): Option[SecureSocialUser] = {
    coll.findOne(MongoDBObject("oauthClients.client_id" -> clientId, "oauthClients.client_secret" -> clientSecret, "oauthClients.grant_type" -> "client_credentials"))  match {
      case Some(obj) => Some(obj)
      case None => None
    }

  }

// OAuth Methodes Account
  def authenticate(email: String, password: String): Option[SecureSocialUser] = {

    coll.findOne(MongoDBObject("profile.userId" -> email, "profile.passwordInfo.password" -> new PasswordHasher.Default().hash(password)))  match {
      case Some(obj) => Some(obj)
      case None => None
    }
  }

  // OAuth Methodes AccessToken
  def create(secureSocialUser: SecureSocialUser, client: OauthClient): Oauth_access_token = {
    def randomString(length: Int) = new Random(new SecureRandom()).alphanumeric.take(length).mkString
    val accessToken = randomString(40)
    val refreshToken = randomString(40)
    val createdAt = System.currentTimeMillis / 1000

    val oauthAccessToken = new Oauth_access_token(
      access_token = accessToken,
      refresh_token = refreshToken,
      created_at = createdAt
    )
    return oauthAccessToken
/*
    val old = new MongoDBObject(obj)
    val updated = SecureSocialUser(old.uuid, old.admin, profile)
    coll.update(MongoDBObject("uuid" -> old.uuid), updated, upsert = false)
    updated





    val generatedId = OauthAccessToken.createWithNamedValues(
      column.accountId -> oauthAccessToken.accountId,
      column.oauthClientId -> oauthAccessToken.oauthClientId,
      column.accessToken -> oauthAccessToken.accessToken,
      column.refreshToken -> oauthAccessToken.refreshToken,
      column.createdAt -> oauthAccessToken.createdAt
    )
    oauthAccessToken.copy(id = generatedId)
    */
  }

  def findByAuthorized(secureSocialUser: SecureSocialUser, clientId: String): Option[Oauth_access_token] = {

    val oat = "oat"
    val oac = "oac"

    coll.findOne(MongoDBObject("uuid" -> oat, "oauthClients.client_id" -> oac))  match {
      case Some(obj) => Some(obj.oauthClients.head.oauth_access_token)
      case None => None
    }

  }
/*
  def findByRefreshToken(refreshToken: String): Option[Oauth_access_token] = {
    val expireAt = new DateTime().minusMonths(1)
    val oat = "oat"
    val find =
    coll.findOne(MongoDBObject("oauthclients.oauth_access_token.refresh_token" -> refreshToken, "oauthclients.oauth_access_token.created_at $gt" -> expireAt))  match {
      case Some(obj) => Some(obj.oauthClients.head.oauth_access_token)
      case None => None
    }
  }
*/
  /** Implicit Salat Conversions */
  implicit def User2DBObj(u: SecureSocialUser): DBObject = grater[SecureSocialUser].asDBObject(u)

  implicit def DBObj2User(obj: DBObject): SecureSocialUser = grater[SecureSocialUser].asObject(obj)

  implicit def MDBObj2User(obj: MongoDBObject): SecureSocialUser = grater[SecureSocialUser].asObject(obj)



}