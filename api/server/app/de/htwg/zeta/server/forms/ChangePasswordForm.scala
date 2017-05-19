package de.htwg.zeta.server.forms

import play.api.data.Form
import play.api.data.Forms

/**
 * The `Change Password` form.
 */
object ChangePasswordForm {

  /**
   * A play framework form.
   */
  val form = Form(Forms.tuple(
    "current-password" -> Forms.nonEmptyText,
    "new-password" -> Forms.nonEmptyText
  ))
}
