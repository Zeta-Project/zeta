
package client

import controller.CodeEditorController

import org.scalajs.dom
import org.scalajs.jquery._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

@JSExport
object Main extends js.JSApp {

  def main(): Unit = {}

  @JSExport
  def main(uuid: String, dslType: String): Unit = jQuery(dom.document).ready { () =>
    println("called editor with " + uuid + " and " + dslType)
    val editorController = new CodeEditorController(
      dslType = dslType,
      metaModelUuid = uuid)
  }

}