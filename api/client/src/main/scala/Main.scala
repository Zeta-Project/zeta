package client

import java.util.UUID

import controller.ModeController
import controller.CodeEditorController
import org.scalajs.dom
import org.scalajs.jquery
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

@JSExport
object Main extends js.JSApp {

  def main(): Unit = {}

  @JSExport
  def main(metaModelId: UUID, dslType: String): Unit = jquery.jQuery(dom.document).ready { () =>
    if (ModeController.getAllModesForModel(metaModelId).keySet.contains(dslType)) {
      CodeEditorController(dslType, metaModelId)
    } else {
      jquery.jQuery("#editor").text("No language \"" + dslType + "\" defined.")
    }
  }

}
