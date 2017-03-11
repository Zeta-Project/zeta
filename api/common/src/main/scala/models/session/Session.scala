package models.session

import play.api.mvc.RequestHeader
import scala.concurrent.Future

/*
 * Retrieve a session for a specific user
 */
trait Session {
  def getSession(user: String, ttl: Long): Future[String]
  def getUser(request: RequestHeader): Future[String]
  def getUser(session: String): Future[String]
}
