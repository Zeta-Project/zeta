package models

import _root_.java.util.UUID

import com.mongodb.ServerAddress
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat._
import models.AppConfig._
import play.api.{Logger, Play}
import securesocial.core._
import securesocial.core.providers.MailToken
import securesocial.core.services.{SaveMode, UserService}

import scala.concurrent.Future

/** Extend this to store custom info for users */
case class SecureSocialUser(uuid: UUID, admin: Boolean = false, profile: BasicProfile)


/** User Service Object implements SecureSocial Users */
object MongoDbUserService extends UserService[SecureSocialUser]{

  import com.mongodb.casbah.commons.conversions.scala._
  RegisterJodaTimeConversionHelpers()

  val log = Logger(this getClass() getName())


  val server = new ServerAddress(MONGO_URL, MONGO_PORT)
 // val credentials = MongoCredential.createMongoCRCredential(MONGO_DB_USER, MONGO_DB_NAME, MONGO_DB_PWD.toCharArray)
  val mongoClient = MongoClient(server)//, List(credentials))
  val db = mongoClient(MONGO_DB_NAME)
  val coll= db(MONGO_USER_DB_NAME)
  val tokenColl = db(MONGO_TOKENS_DB_NAME)


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
      coll.findOne(MongoDBObject("profile" -> ("profile.email" -> email, "profile.providerId" -> providerId))) match {
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
      val updated = new SecureSocialUser(user.uuid, user.admin, user.profile.copy(passwordInfo = Some(info)))
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
        val created = new SecureSocialUser(UUID.randomUUID(), false, profile)
        coll.save(created)
        created

      case Some(obj) =>
        log.debug(profile.toString)
        val old = new MongoDBObject(obj)
        val updated = SecureSocialUser(old.uuid, old.admin, profile)
        coll.update(MongoDBObject("uuid" -> old.uuid), updated, upsert = false)
        updated
    }
  )


  def save(profile: BasicProfile, admin: Boolean,  mode: SaveMode): Future[SecureSocialUser] =  Future.successful(
    coll.findOne(MongoDBObject("profile.userId" -> profile.userId, "profile.providerId" -> profile.providerId)) match {
      case None =>
        log.debug(profile.toString)
        log.error("save mit bool")
        val created = new SecureSocialUser(UUID.randomUUID(), admin, profile)
        coll.save(created)
        created

      case Some(obj) =>
        log.debug(profile.toString)
        val old = new MongoDBObject(obj)
        val updated = SecureSocialUser(old.uuid, admin, profile)
        coll.update(MongoDBObject("uuid" -> old.uuid), updated, upsert = false)
        updated
    }
  )

  def makeAdmin(email: String): Boolean = {
    setAdminStatus(email, true)
  }

  def revokeAdmin(email: String): Boolean = {
    setAdminStatus(email, false)
  }

  def setAdminStatus(email: String, admin: Boolean): Boolean = {
    coll.findOne(MongoDBObject("profile.userId" -> email)) match {
      case Some(obj) =>
        val user = new MongoDBObject(obj)
        val update = $set("admin" -> admin)
        coll.update(user, update)
        true

      case None =>
        false
    }
  }

  /** Salat conversions */
  implicit val ctx = new Context{
    val name ="Custom_Salat_Context"
  }
  ctx.registerClassLoader(Play.classloader(Play.current))

  implicit def User2DBObj(u: SecureSocialUser) : DBObject = grater[SecureSocialUser].asDBObject(u)
  implicit def DBObj2User(obj: DBObject) : SecureSocialUser = grater[SecureSocialUser].asObject(obj)
  implicit def MDBObj2User(obj: MongoDBObject) : SecureSocialUser = grater[SecureSocialUser].asObject(obj)
}