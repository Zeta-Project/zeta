package client

import controller.ModeController
import controller.CodeEditorController
import org.scalajs.dom
import org.scalajs.jquery._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

@JSExport
object Main extends js.JSApp {

  def main(): Unit = {}

  @JSExport
  def main(metaModelUuid: String, dslType: String): Unit = jQuery(dom.document).ready { () =>
    if (ModeController.getAllModesForModel(metaModelUuid).keySet.contains(dslType)) {
      new CodeEditorController(dslType, metaModelUuid)
    } else {
      jQuery("#editor").text("No language \"" + dslType + "\" defined.")
    }
  }

}
