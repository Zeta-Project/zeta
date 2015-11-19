package controller

import java.util.UUID

import facade._
import org.scalajs.jquery._
import scalot._
import shared.CodeEditorMessage.{DocDeleted, DocAdded, TextOperation}

import view.CodeEditorView
import scalatags.Text.all._

case class CodeEditorController(tgtDiv: String, diagramId: String, metaModelId: String) {

  val view = new CodeEditorView(tgtDiv = tgtDiv, controller = this, metaModelId = metaModelId)
  val ws = new WebSocketConnection(controller = this)
  val clientId = UUID.randomUUID().toString

  var docs = Seq[Client]()

  def docsAddedMessage(msg: DocAdded) = {
    println("Got DocsAddedMessage")
    docs = docs ++ Seq(new Client(str = msg.str, revision = msg.revision, title = msg.title, docType = msg.docType, id = msg.id))
    view.upadteSideBar(docs)
  }

  def addDocument(title: String, docType: String) = {
    val newDoc = new Client(str = "", revision = 0, title = title, docType = docType)
    docs = docs ++ Seq(newDoc)
    view.upadteSideBar(docs)
    ws.sendMessage(DocAdded(
      str = "",
      revision = 0,
      docType = docType,
      title = title,
      id = newDoc.id,
      diagramId = diagramId))
  }

  def docDeleteMessage(id: String) = {
    docs = docs.filter(x => x.id != id)
    view.deletedDoc(id)
    view.upadteSideBar(docs)
  }

  def deleteDocument(id: String) = {
    view.selectedId = ""
    docDeleteMessage(id)
    ws.sendMessage(DocDeleted(id, diagramId))
  }

  def getDocForId(id: String): Client = docs.find(_.id == id).get

  /** Apply changes to the corresponding doc */
  def operationFromRemote(op: TextOperation) = {
    val res = docs.find(x => x.id == op.docId).get.applyRemote(op.op)
    res.apply match {
      case Some(apply) if op.docId == view.selectedId =>
        view.updateView(apply)
      case _ =>
    }
    res.send match {
      case Some(send) => ws.sendMessage(TextOperation(send, op.docId))
      case _ =>
    }
  }

  def operationFromLocal(op: scalot.Operation, docId: String) = {
    docs.find(x => x.id == docId).get.applyLocal(op) match {
      case ApplyResult(Some(send), _) => ws.sendMessage(TextOperation(send, docId))
      case _ =>
    }
  }
}