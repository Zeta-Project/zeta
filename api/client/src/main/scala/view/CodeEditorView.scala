package view

import controller.CodeEditorController
import controller.ModeController

import facade.Delta
import facade.ace
import facade.IEditSession

import org.scalajs.dom

import scalot.Client
import scalot.Operation

import scala.scalajs.js
import scala.scalajs.js.JSConverters.genTravConvertible2JSRichGenTrav
import scala.util.Random

import scalatags.JsDom.all
import scalatags.JsDom.all.stringFrag
import scalatags.JsDom.all.bindJsAny
import scalatags.JsDom.all.bindJsAnyLike

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
      all.div(all.`class` := "container")(
      all.div(all.`class` := "panel panel-default")(
        all.div(all.`class` := "panel-heading")(
          all.h3(all.`class` := "panel-title editor-title")(
            all.strong()(s"$dslType"),
            all.span(
              all.`class` := "btn btn-default glyphicon glyphicon-floppy-disk typcnbtn pull-right",
              all.id := "btn-save",
              all.title := "Save Document",
              all.onclick := { (e: dom.MouseEvent) =>
                {
                  controller.saveCode()
                }
              }
            )
          )
        ),
        all.div(all.`class` := "panel-body editor-body")(
          all.div(all.style := "background-color: gray;")(
            all.div(all.`class` := "editor", all.`id` := aceId)
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
