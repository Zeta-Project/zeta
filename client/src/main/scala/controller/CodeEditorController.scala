package controller

import java.util.UUID

import mapping._
import scalot.Client
import shared.CodeEditorMessage.TextOperation

import view.CodeEditorView

case class CodeEditorController(jsEditorObj: Editor = ace.ace.edit("editor")) {
  val view = new CodeEditorView(jsEditorObj, this)
  val ws = new WebSocketConnection(controller = this)
  val clientId = UUID.randomUUID().toString

  val doc = new Client(str = "",revision = 0,id="Test")
  view.displayDoc(doc)

  /** Apply changes to the corresponding doc */
  def operationFromRemote(op: scalot.Operation) = {
      doc.applyRemote(op) match {
        case Some(response) => ws.sendMessage(TextOperation(response))
        case _ => println("Applied remote op without conflicts!")
      }
  }

  def operationFromLocal(op: scalot.Operation) = {
    doc.applyLocal(op) match {
      case Some(response) => ws.sendMessage(TextOperation(response))
      case _ => println(s"Applied remote op without conflicts!")
    }
    println(s"${doc.str}")
  }
}