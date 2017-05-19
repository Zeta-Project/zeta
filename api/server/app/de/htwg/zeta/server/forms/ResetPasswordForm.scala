package de.htwg.zeta.server.forms

import play.api.data.Form
import play.api.data.Forms

/**
 * The `Reset Password` form.
 */
object ResetPasswordForm {

  /**
   * A play framework form.
   */
  val form = Form(
    "password" -> Forms.nonEmptyText
  )
}
