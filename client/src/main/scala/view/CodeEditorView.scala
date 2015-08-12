package view

import controller.CodeEditorController
import mapping._
import org.scalajs.dom.console
import scalot.{Operation, Client}
import scala.scalajs.js
import js.JSConverters._


object ScalotAceAdaptor {
  /**
   * Convert an Ace-Delta object to a corresponding Scalot Operation
   */
  def aceDeltatoScalotOp(delta: Delta, doc: Document): Operation = {
    console.log(delta)
    val base = doc.positionToIndex(delta.range.start, 0)
    val baseOp = Operation().skip(base)

    lazy val combinedString: String = delta.lines
      .map((line) => line + doc.getNewLineCharacter())
        .reduce((head, tail) => head + tail)

    lazy val docLen = doc.getValue().length

    delta.action match {
      case "insertText" => baseOp.insert(delta.text).skip(docLen - delta.text.length - base)
      case "insertLines" => baseOp.insert(combinedString).skip(docLen - combinedString.length - base)
      case "removeText" => baseOp.delete(delta.text.length).skip(docLen - base)
      case "removeLines" => baseOp.delete(combinedString.length).skip(docLen - base)
    }
  }
}

class CodeEditorView(editor: Editor,
                     controller: CodeEditorController) {
  editor.setTheme("ace/theme/monokai")
  editor.getSession().setMode("scala")
  editor.getSession().getDocument().setValue("")
  editor.setReadOnly(true)
  editor.setOptions(js.Dynamic.literal(
    enableBasicAutocompletion = true,
    enableSnippets = true,
    enableLiveAutocompletion = true
  ))

  // Helper
  var broadcast = true
  var currentId: String = ""

  var session: IEditSession = null

  def displayDoc(doc: Client) = {
    editor.setReadOnly(false)
    session = ace.ace.createEditSession(doc.str, "ace/mode/scala")
    session.on("change", {
      (delta: js.Any) =>
        controller.operationFromLocal(
          ScalotAceAdaptor
            .aceDeltatoScalotOp(delta
            .asInstanceOf[js.Dynamic]
            .selectDynamic("data")
            .asInstanceOf[Delta],
              editor.getSession().getDocument()))
    }: js.Function1[js.Any, Any]
    )
    editor.setSession(session)
  }
}