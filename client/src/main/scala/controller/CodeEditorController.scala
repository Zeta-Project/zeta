package controller

import java.util.UUID

import scalot._
import shared.CodeEditorMessage._
import view.CodeEditorView

case class CodeEditorController(dslType: String, metaModelUuid: String) {

  val view = new CodeEditorView(controller = this, metaModelUuid = metaModelUuid, dslType = dslType)
  val ws = new WebSocketConnection(controller = this, metaModelUuid = metaModelUuid, dslType = dslType)
  val clientId = UUID.randomUUID().toString

  var docs = Seq[Client]()

  def docsAddedMessage(msg: DocAdded) = {
    println("Got DocsAddedMessage")
    docs = docs ++ Seq(new Client(str = msg.str, revision = msg.revision, title = msg.title, docType = msg.docType, id = msg.id))
  }

  def docLoadedMessage(msg: DocLoaded) = {
    println("Got DocLoadedMessage")
    view.displayDoc(getDocForId(msg.id))
  }

  def docNotFoundMessage(msg: DocNotFound) = {
    println("Got DocNotFoundMessage")
    val id = addDocument(msg.metaModelUuid, msg.dslType)
    view.displayDoc(getDocForId(id))
  }

  def addDocument(title: String, docType: String): String = {
    val newDoc = new Client(str = "", revision = 0, title = title, docType = docType)
    docs = docs ++ Seq(newDoc)
    ws.sendMessage(DocAdded(
      str = "",
      revision = 0,
      docType = docType,
      title = title,
      id = newDoc.id,
      dslType = dslType,
      metaModelUuid = metaModelUuid))
    newDoc.id
  }

  def docDeleteMessage(id: String) = {
    docs = docs.filter(x => x.id != id)
    view.deletedDoc(id)
  }

  def deleteDocument(id: String) = {
    docDeleteMessage(id)
    ws.sendMessage(DocDeleted(id, dslType))
  }

  def getDocForId(id: String): Client = docs.find(_.id == id).get

  /** Apply changes to the corresponding doc */
  def operationFromRemote(op: TextOperation) = {
    val res = getDocForId(op.docId).applyRemote(op.op)
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
    getDocForId(docId).applyLocal(op) match {
      case ApplyResult(Some(send), _) => ws.sendMessage(TextOperation(send, docId))
      case _ =>
    }
  }
}