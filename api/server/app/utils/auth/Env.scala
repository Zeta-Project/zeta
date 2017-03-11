package utils.auth

import com.mohiva.play.silhouette.api.Env
import models.User
import models.authenticators.SGCookieAuthenticator

/**
 * The default env.
 */
trait DefaultEnv extends Env {
  type I = User
  type A = SGCookieAuthenticator
}
