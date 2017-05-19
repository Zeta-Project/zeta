package de.htwg.zeta.server.forms

import play.api.data.Form
import play.api.data.Forms

/**
 * The `Forgot Password` form.
 */
object ForgotPasswordForm {

  /**
   * A play framework form.
   */
  val form = Form(
    "email" -> Forms.email
  )
}
