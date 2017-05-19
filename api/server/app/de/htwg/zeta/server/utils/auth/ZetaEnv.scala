package de.htwg.zeta.server.utils.auth

import com.mohiva.play.silhouette.api.Env
import models.User
import de.htwg.zeta.server.models.authenticators.SGCookieAuthenticator

/**
 * The default env.
 */
trait ZetaEnv extends Env {
  type I = User
  type A = SGCookieAuthenticator
}
