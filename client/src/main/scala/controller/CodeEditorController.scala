package controller

import java.util.UUID

import mapping._
import scalot._
import shared.CodeEditorMessage.TextOperation

import view.CodeEditorView

case class CodeEditorController(jsEditorObj: Editor = ace.ace.edit("editor")) {
  val view = new CodeEditorView(jsEditorObj, this)
  val ws = new WebSocketConnection(controller = this)
  val clientId = UUID.randomUUID().toString

  val doc = new Client(str = "", revision = 0, id = "Test")
  view.displayDoc(doc)

  /** Apply changes to the corresponding doc */
  def operationFromRemote(op: scalot.Operation) = {
    val res = doc.applyRemote(op)
    res.apply match {
      case Some(apply) => view.updateView(apply)
      case _ =>
    }
    res.send match {
      case Some(send) => ws.sendMessage(TextOperation(send))
      case _ =>
    }
  }

  def operationFromLocal(op: scalot.Operation) = {
    doc.applyLocal(op) match {
      case ApplyResult(Some(response), _) => ws.sendMessage(TextOperation(response))
      case _ => println(s"Applied remote op without conflicts!")
    }
    println(s"${doc.str}")
  }
}