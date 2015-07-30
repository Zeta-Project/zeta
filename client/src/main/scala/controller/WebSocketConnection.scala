package controller

import org.scalajs.dom
import org.scalajs.dom.{CloseEvent, ErrorEvent, Event, MessageEvent, WebSocket, console}
import shared.CodeEditorMessage
import shared.WebSocketMessages.{GetDocs, DocsAdded, DocChanged}
import upickle.default._

case class WebSocketConnection(uri:String = "ws://127.0.0.1:9000/socket", controller: CodeEditorController) {

  /** Set up WebSocket connection */
  val ws = new dom.WebSocket(uri)
  ws.onmessage = (msg: MessageEvent) => onMessage(msg)
  ws.onopen = (e: Event) => onOpen(e)
  ws.onerror = (e: ErrorEvent) =>  console.error(s"Websocket Error! ${e.message}")
  ws.onclose = (e: CloseEvent) => console.log(s"Closed WS for Reason: ${e.reason}")

  def onOpen(e: Event) = {
    console.log("Opened Websocket: ", e.toString)
    sendMessage(GetDocs)
  }

  def onMessage(msg: MessageEvent) = {
    console.log(s"Got Message $msg.message")

    read[CodeEditorMessage](msg.data.toString) match {

      case msg: DocChanged =>
        console.log("Received Change Message" + msg.toString)
        controller.receivedChangeMessage(msg)

      case msg: DocsAdded =>
        console.log("Got DocsAdded message")
        controller.receivedAddDocsMessage(msg)
      case _ => console.error("Unknown message received from Server!")
    }
  }

  def sendMessage(msg: CodeEditorMessage) = ws.readyState match {
    case WebSocket.OPEN =>
      ws.send(write(msg))
    case _ =>
      console.error("Could not send Message because WebSocket is not in ready state!", ws)
  }
}
