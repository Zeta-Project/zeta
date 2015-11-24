
package client

import controller.CodeEditorController

import org.scalajs.dom
import org.scalajs.jquery._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

@JSExport
object Main extends js.JSApp {

  @JSExport
  def main(): Unit = jQuery(dom.document).ready { () =>
    println("MAIN")
  }

  @JSExport
  def editor(uuid: String, editorType: String): Unit = jQuery(dom.document).ready { () =>
    println("called editor with " + uuid + " and " + editorType)
    val editorController = new CodeEditorController(
      tgtDiv = "editor",
      diagramId = editorType,
      metaModelId = uuid)
  }

}