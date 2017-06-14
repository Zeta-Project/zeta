package de.htwg.zeta.common.models.session
import play.api.mvc.RequestHeader

import scala.concurrent.Future

/**
 * Created by user on 3/9/17.
 */
class DummySession extends Session {
  override def getSession(user: String, ttl: Long): Future[String] = Future.successful("")

  override def getUser(request: RequestHeader): Future[String] = Future.successful("")

  override def getUser(session: String): Future[String] = Future.successful("")
}
