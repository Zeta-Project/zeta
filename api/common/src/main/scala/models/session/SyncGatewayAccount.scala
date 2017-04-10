package models.session

import java.util.UUID
import play.api.libs.json._
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scala.concurrent.Promise

/**
 * A Sync Gateway user account.
 *
 * @param name
 * @param password
 * @param admin_channels
 */
case class Account(name: String, admin_channels: List[String], password: String = UUID.randomUUID().toString)

object Account {
  implicit val formatAccount = Json.format[Account]

  def apply(name: String): Account = new Account(name, admin_channels = List(s"ch-dev-${name}", "ch-public"))
}

object SyncGatewayAccount {
  def apply()(implicit client: WSClient): SyncGatewayAccount = new SyncGatewayAccount()
}

/**
 * Session to access the Sync Gateway
 */
class SyncGatewayAccount()(implicit client: WSClient) {
  import scala.concurrent.ExecutionContext.Implicits.global

  val url = "http://database:4985/db/_user/"

  /**
   * Create a Sync Gateway account
   *
   * @param account The Account to create
   */
  def create(account: Account): Future[Unit] = {
    val p = Promise[Unit]

    client.url(url).post(Json.toJson(account)).map { response =>
      if (response.status == 201) {
        p.success()
      } else {
        p.failure(new Exception(response.statusText))
      }
    }.recover {
      case e: Exception => p.failure(e)
    }

    p.future
  }
}
