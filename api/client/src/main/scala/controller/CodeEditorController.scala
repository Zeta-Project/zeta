package controller

import java.util.UUID

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.JSON
import org.scalajs.dom.console
import org.scalajs.jquery
import org.scalajs.jquery.JQueryAjaxSettings
import org.scalajs.jquery.JQueryXHR
import view.CodeEditorView

case class CodeEditorController(dslType: String, metaModelId: UUID) {

  val view = new CodeEditorView(controller = this, metaModelId = metaModelId, dslType = dslType, autoSave = true)

  /**
   * Saves the code via the REST API in the database.
   * Before we can access the REST API, we have to get an oAuth access token.
   * The function authorized() checks, if there is an access token already and if it is still valid.
   * authorized() takes a function, here fnSave(), that takes the valid token and some information about it as parameter.
   * This function fnSave() is a callback function which will be called inside authorized().
   */
  def saveCode(): js.Dynamic = {
    console.log("saveCode()")
    jquery.jQuery.ajax(literal(
      `type` = "PUT",
      url = s"/rest/v1/meta-models/$metaModelId/$dslType",
      contentType = "application/json; charset=utf-8",
      dataType = "json",
      data = JSON.stringify(js.Dictionary(
        "code" -> "" //document.str // TODO insert text from ace-editor
      )),
      success = { (data: js.Dynamic, textStatus: String, jqXHR: JQueryXHR) =>
      },
      error = { (jqXHR: JQueryXHR, textStatus: String, errorThrown: String) =>
        console.log(s"Cannot save: $errorThrown")
      }
    ).asInstanceOf[JQueryAjaxSettings])
  }


}
