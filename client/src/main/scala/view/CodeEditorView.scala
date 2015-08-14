package view

import controller.CodeEditorController
import mapping._
import org.scalajs.dom.console
import scalot._
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

  /**
   * Convert a Scalot Operation into a sequence of ace deltas
   */
  def scalotOpToAceDelta(op: Operation, doc: Document): Seq[Delta] = {
    var deltas: Seq[Delta] = Seq[Delta]()
    def idxToPos(idx: Int) = doc.indexToPosition(idx, 0)
    var base = 0
    for (comp <- op.ops) {
      comp match {
        case SkipComp(x) => base = base + x

        case DelComp(len) =>
          deltas ++= Seq(
            js.Dynamic.literal(
              action = "removeText",
              range = js.Dynamic.literal(
                start = idxToPos(base),
                end = idxToPos(base + len)).asInstanceOf[Range],
              text = doc.getValue().substring(base, len)
            ).asInstanceOf[Delta]
          )

        case InsComp(str) =>
          deltas ++= Seq(
            js.Dynamic.literal(
              action = "insertText",
              range = js.Dynamic.literal(
                start = idxToPos(base),
                end = idxToPos(base + str.length)).asInstanceOf[Range],
              text = str
            ).asInstanceOf[Delta]
          )
          base = base + str.length
      }
    }
    deltas
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
        if(broadcast) {
          println("Sending!")
          controller.operationFromLocal(
            ScalotAceAdaptor
              .aceDeltatoScalotOp(delta
              .asInstanceOf[js.Dynamic]
              .selectDynamic("data")
              .asInstanceOf[Delta],
                editor.getSession().getDocument()))
        }
    }: js.Function1[js.Any, Any]
    )
    editor.setSession(session)
  }

  def updateView(op: Operation) = {
    val was = broadcast
    broadcast = false
    val doc = editor.getSession().getDocument()
    println("Applying!")
    doc.applyDeltas(ScalotAceAdaptor.scalotOpToAceDelta(op,doc).toJSArray)
    broadcast = was
  }
}