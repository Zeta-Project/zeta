
package client

import controller.CodeEditorController

import org.scalajs.dom
import org.scalajs.jquery._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport


object Main extends js.JSApp {

  @JSExport
  def main(): Unit = jQuery(dom.document).ready { () =>
    val editorController = new CodeEditorController("editor", "fakeDiagramId")
  }
}