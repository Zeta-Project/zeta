package models.session

import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.RequestHeader

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise

object SyncGatewaySession {
  def apply()(implicit client: WSClient): SyncGatewaySession = new SyncGatewaySession()
}

/**
 * Session to access the Sync Gateway
 */
class SyncGatewaySession()(implicit client: WSClient) extends Session {

  val url = "http://database:4985/db/_session"

  /**
   * Get a Session for the Sync Gateway
   *
   * @param user The user for which to get the session
   * @param ttl The time in seconds, how long the session is valid
   * @return Return a string which can be used as a session cookie
   */
  def getSession(user: String, ttl: Long): Future[String] = {
    val p = Promise[String]

    val json: JsValue = Json.obj(
      "name" -> user,
      "ttl" -> ttl
    )

    client.url(url)
      .post(json).map { response =>
        (response.json \ "session_id").toOption match {
          case Some(x) => p.success(x.as[String])
          case None => p.failure(new Exception(s"Unable to get a session for ${user} from the database."))
        }
      }.recover {
        case e: Exception => p.failure(e)
      }
    p.future
  }

  /**
   * Get the Sync Gateway user from the session of a request.
   *
   * @param request The request from which to get the user
   * @return The name of the user
   */
  def getUser(request: RequestHeader): Future[String] = {
    request.cookies.get("SyncGatewaySession") match {
      case Some(cookie) => getUser(cookie.value)
      case None => Future.failed(new Exception("No valid session"))
    }
  }

  /**
   * Get the Sync Gateway user from the session string
   * @param session The Session string
   * @return The name of the user
   */
  def getUser(session: String): Future[String] = {
    val p = Promise[String]

    client.url(url).withHeaders("Cookie" -> s"SyncGatewaySession=${session};")
      .get.map { response =>
        (response.json \ "userCtx" \ "name").toOption match {
          case Some(x) => p.success(x.as[String])
          case None => p.failure(new Exception(s"Unable to get the user from the provided session."))
        }
      }.recover {
        case e: Exception => p.failure(e)
      }

    p.future
  }
}
