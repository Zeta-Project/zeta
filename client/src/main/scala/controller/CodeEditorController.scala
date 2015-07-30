package controller

import java.util.UUID

import mapping._
import org.scalajs.dom.console
import shared._
import shared.WebSocketMessages.{DocsAdded, DocChanged}
import view.CodeEditorView

case class CodeEditorController(jsEditorObj: Editor = ace.ace.edit("editor")) {

  val view = new CodeEditorView(jsEditorObj, this)
  val ws = new WebSocketConnection(controller = this)

  var docs = Seq[WootDoc]()
  val clientId = UUID.randomUUID().toString

  /** Gets called when the user changes the document */
  def docEdited(msg: DocChanged) = ws.sendMessage(msg)

  def addNewDocument(doc: WootDoc) = {
    console.log("Adding new Document! with uuid: " + doc.uuid)
    val newDoc = new WootDoc(
      woot = doc.woot.copy(site = new SiteId(clientId))
    )
    docs :+= newDoc
    console.log("Sending DocsAdded message")
    view.displayDoc(newDoc)
    ws.sendMessage(DocsAdded(Seq[WootDoc](newDoc)))
  }

  /** Apply changes to the corresponding doc */
  def receivedChangeMessage(msg: DocChanged) = {
    console.log("About to ingest changes")
    console.log(msg.doc.woot.site.value != clientId)
    if (msg.doc.woot.site.value != clientId) {
      val was = view.broadcast
      view.broadcast = false
      console.log("Got" + msg.doc.uuid)
      docs.foreach( x => console.log(x.uuid))

      docs.filter(_.uuid == msg.doc.uuid).foreach(
        x => {
          console.log("Ingesting changes …")
          view.updateView(msg)
          x.ingestChangeMessage(msg)
        }
      )
      view.broadcast = was
    } else {
      console.error("Got my own delta message!")
    }
  }

  /** Add New Docs */
  def receivedAddDocsMessage(msg: DocsAdded) = {
    console.log(s"about to add ${msg.docs.size} new documents")
    msg.docs.foreach(
      (x) => docs.forall(_.uuid != x.uuid) match {
        case true =>
          console.log("Adding Doc!")
          docs :+= x.copy(woot = x.woot.copy(site = new SiteId(clientId)))
          view.displayDoc(doc = docs.last)
        case _ =>
          console.error("Trying to add doc which already existed!")
      }
    )
  }
}