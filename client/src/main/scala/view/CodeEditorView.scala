package view

import controller.{ModeController, CodeEditorController}
import facade._
import org.scalajs.dom
import scalot._
import scala.scalajs.js
import js.JSConverters._
import org.scalajs.jquery._
import scala.util.Random
import scalatags.JsDom.all._
import scalajs.js.Dynamic.literal
import facade.JQueryUi._

class CodeEditorView(tgtDiv: String, controller: CodeEditorController, metaModelId: String) {

  private val aceId = Random.alphanumeric.take(20).mkString

  createSkeleton()
  upadteSideBar(Seq[Client]())

  val editor = ace.ace.edit(s"$aceId")
  editor.setTheme("ace/theme/xcode")
  editor.getSession().setMode("scala")
  editor.$blockScrolling = Double.PositiveInfinity

  editor.setOptions(js.Dynamic.literal(
    enableBasicAutocompletion = true,
    enableSnippets = true,
    enableLiveAutocompletion = true
  ))

  jQuery(".ace-container .editor").hide()
  var selectedId: String = ""

  private def renderNewDocumentForm() = div(
    div(`class` := "form-group")(
      label(`for` := "titel")("Title:"),
      input(`type` := "titel", `class` := "form-control", `id` := "newDocTitle")("Titel")
    ),
    div(`class` := "form-group")(
      label(`for` := "doctype")("File Type"),
      select(`type` := "doctype", `class` := "form-control", `id` := "newDocType")
      (for (mode <- ModeController.getAllModesForModel(metaModelId).keysIterator.toArray) yield option(mode))
    )
  ).render

  private def createSkeleton() =
    dom.document.getElementById(s"$tgtDiv").appendChild(
      div(`class` := "ace-container container")(
        div(`class` := "row")(
          div(`class` := "toolbar")(
            span(
              `class` := "btn btn-default typcn typcn-document-add toolbarbtn typcnbtn",
              onclick := { (e: dom.MouseEvent) => {
                Bootbox.bootbox.dialog(literal(
                  title = "Please enter a document title and filetype",
                  message = renderNewDocumentForm(),
                  closeButton = true,
                  buttons = literal(
                    ok = literal(label = "Cancel", callback = () => {}),
                    cancel = literal(label = "Add", callback = () => {
                      controller.addDocument(jQuery("#newDocTitle").`val`().asInstanceOf[String], jQuery("#newDocType").`val`().toString)
                    }
                    )
                  )
                ))
              }
              }
            ).render,
            span(`class` := "btn btn-default typcn typcn-document-delete toolbarbtn typcnbtn",
              onclick := { (e: dom.MouseEvent) => {
                if (selectedId != "") {
                  Bootbox.bootbox.confirm(
                    s"Are you sure that you want to delete '${controller.getDocForId(selectedId).title}'?",
                    (result: Boolean) => if (result) controller.deleteDocument(selectedId))
                }
              }
              })
          )
        ),
        div(`class` := "row")(
          div(`id` := "sidebar", `class` := "col-md-4")(),
          div(style := "background-color: gray;")(
            div(`class` := "editor col-md-8", `id` := aceId)
          )
        )
      ).render
    )

  def upadteSideBar(docs: Seq[Client]) = {
    jQuery(s"#$tgtDiv .ace-container .row #sidebar #selectable").remove()
    val sidebar = jQuery(
      ol(`id` := "selectable")(
        for (file <- docs) yield {
          li(`class` := s"ui-widget-content ${
            if (file.id == selectedId) {
              "ui-selected"
            }
          }",
            `id` := file.id,
            onclick := { (e: dom.MouseEvent) => {
              jQuery(s".ui-widget-content").removeClass("ui-selected")
              selectedId = file.id
              displayDoc(controller.getDocForId(file.id))
              jQuery(s"#${file.id}").addClass("ui-selected")
            }
            })(
            span(
              file.title,
              span(`class` := "typcn typcn-document pull-right"),
              span(`style` := "color: gray;", `class` := "pull-right")(file.docType)
            )
          ).render
        }
      ).render
    )
    sidebar.sortable()
    sidebar.appendTo(jQuery(s"#$tgtDiv .ace-container .row #sidebar"))
  }

  var broadcast = true
  var currentId: String = ""
  var session: IEditSession = null

  def displayDoc(doc: Client) = {
    session = ace.ace.createEditSession(
      doc.str,
      ModeController.getAllModesForModel(metaModelId)(doc.docType))
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
                editor.getSession().getDocument()),
            selectedId)
        }
    }: js.Function1[js.Any, Any])
    editor.setSession(session)
    jQuery(".ace-container .editor").show()
  }

  def updateView(op: Operation) = {
    val was = broadcast
    broadcast = false
    val doc = editor.getSession().getDocument()
    doc.applyDeltas(ScalotAceAdaptor.scalotOpToAceDelta(op, doc).toJSArray)
    broadcast = was
  }

  def deletedDoc(id: String) = {
    if (selectedId == id) {
      selectedId = ""
      jQuery(".ace-container .editor").hide()
    }
  }
}