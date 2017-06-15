package de.htwg.zeta.server.util.auth

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import de.htwg.zeta.common.models.entity.User

/**
 * The default environment.
 */
trait ZetaEnv extends Env {

  /** The Identity. */
  type I = User

  /** The Authenticator. */
  type A = CookieAuthenticator

}
