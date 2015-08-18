package view

import controller.CodeEditorController
import facade._
import org.scalajs.dom
import org.scalajs.dom.console
import org.scalajs.dom.raw.MouseEvent
import scalot._
import scala.scalajs.js
import js.JSConverters._
import org.scalajs.jquery._
import scala.util.Random
import scalatags.JsDom.all._
import scalajs.js.Dynamic.literal
import facade.JQueryUi._

class CodeEditorView(tgtDiv: String,
                     controller: CodeEditorController) {

  val files = Seq[String]("File1", "File12", "File42")
  private val aceId = Random.alphanumeric.take(20).mkString

  createSkeleton()
  renderSideBar()
  val editor = ace.ace.edit(s"$aceId")

  editor.setTheme("ace/theme/xcode")
  editor.getSession().setMode("scala")

  editor.getSession().getDocument().setValue("")

  editor.setReadOnly(true)

  editor.setOptions(js.Dynamic.literal(
    enableBasicAutocompletion = true,
    enableSnippets = true,
    enableLiveAutocompletion = true
  ))

  private def createSkeleton() =
    dom.document.getElementById(s"$tgtDiv").appendChild(
      div(`class` := "ace-container container")(
        div(`class` := "row")(
          div(`class` := "toolbar")(
            span(
              `class` := "btn btn-default typcn typcn-document-add toolbarbtn",
              onclick := { (e: dom.MouseEvent) => {
                Alertify.alertify.prompt(
                    "Please enter a document title",
                    "New Document",
                    (_: js.Any, x: js.Any) => println("Adding new doc with name: "+x.toString))
                  .set(literal())
              }
              }
            ).render
            ,
            span(`class` := "btn btn-default typcn typcn-document-delete toolbarbtn",
              onclick := { (e: dom.MouseEvent) => {
                Alertify.alertify.confirm(
                  "Are you sure that you want to delete the file?",
                  (confirmed: Boolean) => println("Deleting document!"))
              }
              })
          )
        ),
        div(`class` := "row")(
          div(`id` := "sidebar", `class` := "col-md-4")(),
          div(`class` := "editor col-md-8", `id` := aceId)
        )
      ).render
    )



  private def renderSideBar() = {
    val sidebar = jQuery(
      ol(`id` := "selectable")(
        for (file <- files) yield {
          li(`class` := "ui-widget-content",
            onclick := { (e: dom.MouseEvent) => {
              jQuery(s"#$tgtDiv #selectable").children().removeClass("ui-selected")
              jQuery(e.srcElement).addClass("ui-selected")
            }
            })(file).render
        }
      ).render
    )
    sidebar.sortable()
    sidebar.appendTo(jQuery(s"#$tgtDiv .ace-container .row #sidebar"))
  }

  // Helper
  var broadcast = true
  var currentId: String = ""

  var session: IEditSession = null

  val sendBtn = jQuery(
    div(`class` := "btn btn-default col-md-offset-5")(
      "Send next Msg To Server"
    ).render)
  sendBtn.appendTo(jQuery("body"))
  sendBtn.click(() => controller.ws.sendFromBuffer())

  val receiveBtn = jQuery(
    div(`class` := "btn btn-default")(
      "Receive From Buffer"
    ).render)
  receiveBtn.appendTo(jQuery("body"))
  receiveBtn.click(() => controller.ws.receiveFromBuffer())

  def displayDoc(doc: Client) = {
    editor.setReadOnly(false)
    session = ace.ace.createEditSession(doc.str, "ace/mode/scala")
    session.on("change", {
      (delta: js.Any) =>
        if (broadcast) {
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
    doc.applyDeltas(ScalotAceAdaptor.scalotOpToAceDelta(op, doc).toJSArray)
    broadcast = was
  }
}