package controller

import org.scalajs.dom
import org.scalajs.dom.{CloseEvent, ErrorEvent, Event, MessageEvent, WebSocket, console}
import shared.CodeEditorMessage
import shared.CodeEditorMessage.{DocDeleted, DocAdded, TextOperation}
import upickle.default._

import scala.collection.mutable
import org.scalajs.jquery._
import scalatags.Text.all._

case class WebSocketConnection(uri: String = "ws://127.0.0.1:9000/socket", controller: CodeEditorController) {

  /** Set up WebSocket connection */
  val ws = new dom.WebSocket(uri)
  ws.onmessage = (msg: MessageEvent) => onMessage(msg)
  ws.onopen = (e: Event) => onOpen(e)
  ws.onerror = (e: ErrorEvent) => console.error(s"Websocket Error! ${e.message}")
  ws.onclose = (e: CloseEvent) => console.log(s"Closed WS for Reason: ${e.reason}")

  def onOpen(e: Event) = {
    console.log("Opened Websocket: ", e.toString)
  }

  def onMessage(msg: MessageEvent) = {
    read[CodeEditorMessage](msg.data.toString) match {
      case msg: TextOperation => controller.operationFromRemote(msg)
      case msg: DocAdded => controller.docsAddedMessage(msg)
      case msg: DocDeleted => controller.docDeleteMessage(msg.id)
      case _ =>
    }
  }

  def sendMessage(msg: CodeEditorMessage) = ws.readyState match {
    case WebSocket.OPEN =>
      ws.send(write(msg))
    case _ =>
      console.error("Could not send Message because WebSocket is not in ready state!", ws)
  }
}
