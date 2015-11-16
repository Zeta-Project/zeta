package models

case class Account(id: String, email: String)

object Account {

  def authenticate(email: String, password: String): Option[Account] = {
    MongoDbUserService.authenticate(email, password).map { u =>
      Account(u.profile.userId, u.profile.email.getOrElse("invalid mail"))
    }
  }

  def findOneById(id: String): Option[Account] = MongoDbUserService.findOneById(id).map  { u =>
    Account(u.profile.userId, u.profile.email.getOrElse("invalid mail"))
  }

}
