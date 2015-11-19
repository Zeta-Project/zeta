
package client

import controller.CodeEditorController

import org.scalajs.dom
import org.scalajs.jquery._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport


object Main extends js.JSApp {

  @JSExport
  def main(): Unit = jQuery(dom.document).ready { () =>
    val editorController = new CodeEditorController(
      tgtDiv = "editor",
      diagramId = "fakeDiagramId",
      metaModelId = "4357a224-5fed-43d5-b528-639f39d423a0")
  }

}