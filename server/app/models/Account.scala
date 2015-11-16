package models

import java.security.MessageDigest

import com.mongodb.casbah.Imports._
import com.novus.salat.annotations._
import com.novus.salat.dao.{ModelCompanion, SalatDAO}
import org.joda.time.DateTime
import models.custom_context._

case class Account(@Key("_id") id: ObjectId, email: String, password: String, createdAt: DateTime)

object Account extends ModelCompanion[Account, ObjectId] {

  val client: MongoClient = MongoClient(MongoClientURI("mongodb://localhost:27017"))
  val collection = client("modigen_v3")("account")
  override val dao = new SalatDAO[Account, ObjectId](collection = collection) {}

  private def digestString(s: String): String = {
    val md = MessageDigest.getInstance("SHA-1")
    md.update(s.getBytes)
    md.digest.foldLeft("") { (s, b) =>
      s + "%02x".format(if (b < 0) b + 256 else b)
    }
  }

  def authenticate(email: String, password: String): Option[Account] = {
    findOne(MongoDBObject("email" -> email, "password" -> digestString(password)))
  }

  // Only for testing purposes
  def create(acc: Account): Option[ObjectId] = {
    insert(acc.copy(password = digestString(acc.password)))
  }

}
