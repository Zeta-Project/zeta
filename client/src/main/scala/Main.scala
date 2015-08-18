
package client

import controller.CodeEditorController

import org.scalajs.dom
import org.scalajs.jquery._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import facade.JQueryUi._
import facade.Alertify.alertify

import js.Dynamic._

object Main extends js.JSApp {

  @JSExport
  def main(): Unit = jQuery(dom.document).ready { () =>
    val editorController = new CodeEditorController("editor")
  }
}