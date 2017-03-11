package view

import controller.{ CodeEditorController, ModeController }
import facade._
import org.scalajs.dom
import scalot._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.util.Random
import scalatags.JsDom.all._

class CodeEditorView(controller: CodeEditorController, metaModelUuid: String, dslType: String, autoSave: Boolean) {

  private val aceId = Random.alphanumeric.take(20).mkString
  var selectedId: String = ""

  createSkeleton()
  if (autoSave) {
    dom.document.getElementById("btn-save").classList.add("hidden")
  }

  val editor = ace.ace.edit(s"$aceId")
  editor.setTheme("ace/theme/xcode")
  editor.getSession().setMode("scala")
  editor.$blockScrolling = Double.PositiveInfinity

  editor.setOptions(js.Dynamic.literal(
    enableBasicAutocompletion = true,
    enableSnippets = true,
    enableLiveAutocompletion = true
  ))

  private def createSkeleton() =
    dom.document.getElementById("editor").appendChild(
      div(`class` := "container")(
      div(`class` := "panel panel-default")(
        div(`class` := "panel-heading")(
          h3(`class` := "panel-title editor-title")(
            strong()(s"$dslType"),
            span(
              `class` := "btn btn-default glyphicon glyphicon-floppy-disk typcnbtn pull-right",
              id := "btn-save",
              title := "Save Document",
              onclick := { (e: dom.MouseEvent) =>
                {
                  controller.saveCode()
                }
              }
            )
          )
        ),
        div(`class` := "panel-body editor-body")(
          div(style := "background-color: gray;")(
            div(`class` := "editor", `id` := aceId)
          )
        )
      )
    ).render
    )

  var broadcast = true
  var session: IEditSession = null

  def displayDoc(doc: Client) = {
    selectedId = doc.id
    session = ace.ace.createEditSession(
      doc.str,
      ModeController.getAllModesForModel(metaModelUuid)(doc.docType)
    )
    session.on("change", {
      (delta: js.Any) =>
        if (broadcast) {
          controller.operationFromLocal(
            ScalotAceAdaptor
              .aceDeltatoScalotOp(
                delta
                .asInstanceOf[js.Dynamic]
                .selectDynamic("data")
                .asInstanceOf[Delta],
                editor.getSession().getDocument()
              ),
            selectedId
          )
        }
    }: js.Function1[js.Any, Any])
    editor.setSession(session)
  }

  def updateView(op: Operation) = {
    val was = broadcast
    broadcast = false
    val doc = editor.getSession().getDocument()
    doc.applyDeltas(ScalotAceAdaptor.scalotOpToAceDelta(op, doc).toJSArray)
    broadcast = was
  }

}