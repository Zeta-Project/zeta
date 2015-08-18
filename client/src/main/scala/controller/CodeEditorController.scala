package controller

import java.util.UUID

import facade._
import org.scalajs.jquery._
import scalot._
import shared.CodeEditorMessage.TextOperation

import view.CodeEditorView
import scalatags.Text.all._

case class CodeEditorController(tgtDiv:String){
  val view = new CodeEditorView(tgtDiv, this)
  val ws = new WebSocketConnection(controller = this)
  val clientId = UUID.randomUUID().toString

  val doc = new Client(str = "", revision = 0, id = "Test")
  view.displayDoc(doc)

  /** Apply changes to the corresponding doc */
  def operationFromRemote(op: scalot.Operation) = {
    println(s"ApplyRemote rev:${op.revision} id:${op.id} current state is: ${doc.state.getClass.getName}  current rev: ${doc.revision}")
    val res = doc.applyRemote(op)
    res.apply match {
      case Some(apply) => view.updateView(apply)
      case _ =>
    }
    res.send match {
      case Some(send) =>
        ws.addToBuffer(TextOperation(send))
      case _ =>
    }
    displayClientState()
  }

  def displayClientState() = {
    jQuery("#clientStateView").html(
      div(
        p("State:" + doc.state.getClass.getSimpleName),
        p("Revision:" + doc.revision)
      ).render
    )
  }

  def operationFromLocal(op: scalot.Operation) = {
    doc.applyLocal(op) match {
      case ApplyResult(Some(response), _) =>
        ws.addToBuffer(TextOperation(response))
      case _ =>
    }
    displayClientState()
  }
}