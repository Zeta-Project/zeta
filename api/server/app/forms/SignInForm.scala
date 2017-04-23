package forms

import play.api.data.Form
import play.api.data.Forms

/**
 * The form which handles the submission of the credentials.
 */
object SignInForm {

  /**
   * A play framework form.
   */
  val form = Form(
    Forms.mapping(
      "email" -> Forms.email,
      "password" -> Forms.nonEmptyText,
      "rememberMe" -> Forms.boolean
    )(Data.apply)(Data.unapply)
  )

  /**
   * The form data.
   *
   * @param email The email of the user.
   * @param password The password of the user.
   * @param rememberMe Indicates if the user should stay logged in on the next visit.
   */
  case class Data(
      email: String,
      password: String,
      rememberMe: Boolean)
}
