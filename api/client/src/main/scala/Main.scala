package client

import java.util.UUID

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

import controller.CodeEditorController
import controller.ModeController
import org.scalajs.dom
import org.scalajs.jquery

@JSExport
object Main extends js.JSApp {

  def main(): Unit = {}

  @JSExport
  def main(metaModelIdAsString: String, dslType: String): Unit = {
    jquery.jQuery(dom.document).ready { () =>
      val metaModelId = UUID.fromString(metaModelIdAsString)
      if (ModeController.getAllModesForModel(metaModelId).keySet.contains(dslType)) {
        CodeEditorController(dslType, metaModelId)
      } else {
        jquery.jQuery("#editor").text("No language \"" + dslType + "\" defined.")
      }
    }
  }

}
