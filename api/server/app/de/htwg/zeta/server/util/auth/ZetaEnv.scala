package de.htwg.zeta.server.util.auth

import com.mohiva.play.silhouette.api.Env
import de.htwg.zeta.common.models.User
import de.htwg.zeta.server.model.authenticators.SGCookieAuthenticator

/**
 * The default env.
 */
trait ZetaEnv extends Env {
  type I = User
  type A = SGCookieAuthenticator
}
