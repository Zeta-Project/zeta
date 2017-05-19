package de.htwg.zeta.server.job

import javax.inject.Inject

import akka.actor.Actor
import com.mohiva.play.silhouette.api.util.Clock
import de.htwg.zeta.server.job.AuthTokenCleaner.Clean
import de.htwg.zeta.server.model.services.AuthTokenService
import grizzled.slf4j.Logging

/**
 * A job which cleanup invalid auth tokens.
 *
 * @param service The auth token service implementation.
 * @param clock   The clock implementation.
 */
class AuthTokenCleaner @Inject()(
    service: AuthTokenService,
    clock: Clock)
  extends Actor with Logging {

  /**
   * Process the received messages.
   */
  def receive: Receive = {
    case Clean =>
      val start = clock.now.getMillis
      val msgBuilder: () => StringBuffer = () => {
        val msg = new StringBuffer("\n")
        msg.append("=================================\n")
        msg.append("Start to cleanup auth tokens\n")
        msg.append("=================================\n")
        msg
      }
      service.clean.map { deleted =>
        info {
          val msg = msgBuilder()
          val seconds = (clock.now.getMillis - start) / 1000
          msg.append("Total of %s auth tokens(s) were deleted in %s seconds".format(deleted.length, seconds)).append("\n")
          msg.append("=================================\n")
          msg.append("=================================\n")
          msg.toString
        }
      }(context.dispatcher).recover {
        case e: Throwable =>
          error({
            val msg = msgBuilder()
            msg.append("Couldn't cleanup auth tokens because of unexpected error\n")
            msg.append("=================================\n")
            msg.toString
          }, e)
      }(context.dispatcher)
  }
}

/**
 * The companion object.
 */
object AuthTokenCleaner {

  case object Clean

}
