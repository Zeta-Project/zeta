package de.htwg.zeta.server.util.auth

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.User

/**
 * The default environment.
 */
trait ZetaEnv extends Env {

  /** The Identity. */
  type I = User

  /** The Authenticator. */
  type A = CookieAuthenticator

}
