
package client

import controller.CodeEditorController
import org.scalajs.dom
import org.scalajs.jquery._
import shared.WootDoc

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalatags.Text.all._
object Main extends js.JSApp {

  var inserting = true

  @JSExport
  def main(): Unit = jQuery(dom.document).ready { () =>

    println("HEllo World!")
    val editorController = new CodeEditorController()

    val btn = jQuery(
      div(`class`:="btn btn-default")(
        "AddDoc"
      ).render)
    btn.appendTo(jQuery("body"))
    btn.click(() => editorController.addNewDocument(new WootDoc()))

  /*  jQuery("body").append(
      )).render)*/


   val editor = ace.edit("editor")
    editor.setTheme("ace/theme/monokai")
    editor.getSession().setMode("ace/mode/scala")
    val text = editor.getValue()

    jQuery("body").append(div(p("Bla")).render)

    editor.getSession().on("change", { (delta: js.Any) =>
      if (!inserting) {
        inserting = true
        val d = delta.asInstanceOf[js.Dynamic].selectDynamic("data")
        console.log(d)
        editor.getSession().doc.applyDeltas(js.Array(d.asInstanceOf[Delta]))
      }
      inserting = false

    }: js.Function1[js.Any, Any] )

    jQuery("#sendBtn").click(() => {
      console.log("Fuu")
    })
  }
}