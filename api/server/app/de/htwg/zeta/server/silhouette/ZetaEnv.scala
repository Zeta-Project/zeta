package de.htwg.zeta.server.silhouette

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator

/**
 * The default environment.
 */
trait ZetaEnv extends Env {

  /** The Identity. */
  type I = ZetaIdentity

  /** The Authenticator. */
  type A = CookieAuthenticator

}
