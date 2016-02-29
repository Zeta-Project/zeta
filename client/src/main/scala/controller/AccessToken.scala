package controller

import java.util.Date

import org.scalajs.jquery._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal

/**
  * Handles the oAuth access token for authorized access to the REST API.
  */
object AccessToken {

  case class Token(token: String, expires: Int, timestampLoaded: Long)

  case class TokenInformation(token: String, refreshed: Boolean, error: Option[String])

  var accessToken: Token = null

  /**
    * Gets or refreshes the oAuth access token if necessary, and calls the callback function which takes the
    * TokenInformation as parameter.
    *
    * @param callback - The callback function.
    * @param forceRefresh - Forces a refresh of the token.
    */
  def authorized(callback: TokenInformation => Unit, forceRefresh: Boolean): Unit = {

    var refresh = forceRefresh

    if (accessToken == null) {
      refresh = true
    } else {
      val currentTime = new Date().getTime
      val difference = (currentTime - accessToken.timestampLoaded) / 1000
      if (difference > accessToken.expires - 10) {
        // refresh if token expires in less than 10 seconds
        refresh = true
      }
    }

    if (refresh) {
      refreshAccessToken(callback)
    } else {
      callback(TokenInformation(accessToken.token, refreshed = false, None))
    }

    def refreshAccessToken(callback: TokenInformation => Unit): Unit = {
      jQuery.ajax(literal(
        `type` = "POST",
        url = "/oauth/accessTokenLocal",
        data = literal(
          client_id = "modigen-browser-app1",
          grant_type = "implicit"
        ),
        success = { (data: js.Dynamic, textStatus: String, jqXHR: JQueryXHR) =>
          accessToken = Token(data.access_token.asInstanceOf[String], data.expires_in.asInstanceOf[Int], new Date().getTime)
          callback(TokenInformation(accessToken.token, refreshed = true, None))
        },
        error = { (jqXHR: JQueryXHR, textStatus: String, errorThrown: String) =>
          accessToken = null
          callback(TokenInformation("", refreshed = true, Some(errorThrown)))
        }
      ).asInstanceOf[JQueryAjaxSettings]
      )
    }
  }

}
