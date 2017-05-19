package de.htwg.zeta.server.forms

import play.api.data.Form
import play.api.data.Forms

/**
 * The form which handles the sign up process.
 */
object SignUpForm {

  /**
   * A play framework form.
   */
  val form = Form(
    Forms.mapping(
      "firstName" -> Forms.nonEmptyText,
      "lastName" -> Forms.nonEmptyText,
      "email" -> Forms.email,
      "password" -> Forms.nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  /**
   * The form data.
   *
   * @param firstName The first name of a user.
   * @param lastName The last name of a user.
   * @param email The email of the user.
   * @param password The password of the user.
   */
  case class Data(
      firstName: String,
      lastName: String,
      email: String,
      password: String)
}
