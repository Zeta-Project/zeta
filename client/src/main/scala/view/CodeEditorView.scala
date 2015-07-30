package view

import controller.CodeEditorController
import mapping._
import org.scalajs.dom.console
import shared.{WootDoc, InsertOp, Operation}
import shared.WebSocketMessages.DocChanged

import scala.scalajs.js
import js.JSConverters._

class CodeEditorView(editor: Editor,
                     controller: CodeEditorController) {
  editor.setTheme("ace/theme/monokai")
  editor.getSession().setMode("scala")
  editor.getSession().getDocument().setValue("")
  editor.setReadOnly(true)
  editor.setOptions(js.Dynamic.literal(
    enableBasicAutocompletion = true,
    enableSnippets =  true,
    enableLiveAutocompletion= true
  ))

  // Helper for Defining
  var broadcast = true
  var currentId: String = ""

  /** Gets called when the document changed */
  private def onDocumentEdit(delta: Delta, changed: WootDoc) = {
    if (broadcast) {
      console.log("DocChanged and Brodcast true")
      controller.docEdited(DocChanged(changed, aceDeltaToWootOps(delta, changed)))
    } else {
      console.log("Busy ingesting changes from server!")
    }
  }

  def updateView(doc: DocChanged) = if (doc.doc.uuid == currentId) {
    editor.getSession()
      .getDocument()
      .applyDeltas(wootOpsToAceDeltas(doc.ops, doc.doc))
  }

  def displayDoc(doc: WootDoc) = {
    console.log("Displaying doc " + doc)
    currentId = doc.uuid
    editor.setReadOnly(false)
    val session = ace.ace.createEditSession(doc.woot.text, "ace/mode/scala")

    // register for Events
    session.on("change", {
      (delta: js.Any) =>
        onDocumentEdit(
          delta.asInstanceOf[js.Dynamic].selectDynamic("data").asInstanceOf[Delta],
          doc)
    }: js.Function1[js.Any, Any]
    )
    editor.setSession(session)
  }

  private def getDoc: Document = editor.getSession().getDocument()

  /** Convert the woot operations to an array of Ace-Deltas */
  private def wootOpsToAceDeltas(ops: Seq[Operation], wootDoc: WootDoc): js.Array[Delta] = {

    def idxToPos(idx: Double) = {
      getDoc.indexToPosition(idx, 0)
    }

    val x = for (op <- ops) yield {
      //console.log(op.wchar.isVisible)
      val idx = wootDoc.woot.visibleIndexOf(op.wchar.id)
      console.log("     -> "+ idx  + "    == ",idxToPos(idx))
       val x = js.Dynamic.literal(
        action = op match {
          case x: InsertOp => "insertText"
          case _ => "removeText"
        },
        range = js.Dynamic.literal(
          start = idxToPos(idx),
          end = idxToPos(idx + 1)).asInstanceOf[Range],
        text = op.wchar.alpha.toString).asInstanceOf[Delta]
      console.log(x)
      x
    }
    x.toJSArray
  }

  /** Converts the ace Delta to WOOT Operations and applies the Delta to the woot model */
  private def aceDeltaToWootOps(delta: Delta, wootDoc: WootDoc): Seq[Operation] = {
    console.log(delta.action)
    delta.action match {
      case "insertText" | "removeText" =>
        val base = getDoc.positionToIndex(delta.range.start, 0).asInstanceOf[Int]
        console.log("DELTA TEXT: "+ delta.text)
        for (x <- 0 to delta.text.length() - 1) yield {
          val tup = delta.action match {
            case "insertText" =>
              console.log(delta.text.charAt(x).toString + "   "+ (x + base))
              wootDoc.woot.insert(delta.text.charAt(x), x + base)
            case _ => wootDoc.woot.delete(base)
          }
          wootDoc.woot = tup._2
          //console.log("UpdatedText: " + wootDoc.woot.text)
          tup._1
        }

      case "insertLines" | "removeLines" =>
        delta.text = delta.lines
          .map((line) => line + getDoc.getNewLineCharacter())
          .reduce((head, tail) => head + tail)

        delta.lines = js.Array[String]()
        delta.action = delta.action.replace("Lines", "Text")
        aceDeltaToWootOps(delta, wootDoc)
    }
  }

}
