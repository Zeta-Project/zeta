package controller

import java.util.UUID

import scalot._
import shared.CodeEditorMessage._
import view.CodeEditorView

case class CodeEditorController(dslType: String, metaModelUuid: String) {

  val view = new CodeEditorView(controller = this, metaModelUuid = metaModelUuid, dslType = dslType)
  val ws = new WebSocketConnection(controller = this, metaModelUuid = metaModelUuid, dslType = dslType)
  val clientId = UUID.randomUUID().toString
  var document: Client = null

  def docLoadedMessage(msg: DocLoaded) = {
    println("Got DocLoadedMessage")
    document = new Client(str = msg.str, revision = msg.revision, title = msg.title, docType = msg.docType, id = msg.id)
    view.displayDoc(document)
  }

  def docNotFoundMessage(msg: DocNotFound) = {
    println("Got DocNotFoundMessage")
    addDocument(msg.metaModelUuid, msg.dslType)
    view.displayDoc(document)
  }

  def addDocument(title: String, docType: String) = {
    document = new Client(str = "", revision = 0, title = title, docType = docType)
    ws.sendMessage(
      DocAdded(str = "", revision = 0, docType = docType, title = title, id = document.id, dslType = dslType, metaModelUuid = metaModelUuid)
    )
  }

  def deleteDocument(id: String) = {
    ws.sendMessage(DocDeleted(id, dslType))
  }

  /** Apply changes to the corresponding doc */
  def operationFromRemote(op: TextOperation) = {
    val res = document.applyRemote(op.op)
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
    document.applyLocal(op) match {
      case ApplyResult(Some(send), _) => ws.sendMessage(TextOperation(send, docId))
      case _ =>
    }
  }
}