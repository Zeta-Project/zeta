package controller

import java.util.UUID

import org.scalajs.dom
import org.scalajs.dom.CloseEvent
import org.scalajs.dom.ErrorEvent
import org.scalajs.dom.Event
import org.scalajs.dom.MessageEvent
import org.scalajs.dom.WebSocket
import org.scalajs.dom.console
import org.scalajs.dom.window
import shared.CodeEditorMessage
import shared.CodeEditorMessage.DocLoaded
import shared.CodeEditorMessage.DocNotFound
import shared.CodeEditorMessage.TextOperation
import upickle.default

case class WebSocketConnection(
    uri: String = s"ws://${window.location.host}/codeeditor/socket",
    controller: CodeEditorController,
    metaModelId: UUID,
    dslType: String) {

  /** Set up WebSocket connection */
  val ws = new dom.WebSocket(uri + "/" + metaModelId + "/" + dslType)
  ws.onmessage = (msg: MessageEvent) => onMessage(msg)
  ws.onopen = (e: Event) => onOpen(e)
  ws.onerror = (e: ErrorEvent) => console.error(s"Websocket Error! ${e.message}")
  ws.onclose = (e: CloseEvent) => console.log(s"Closed WS for Reason: ${e.reason}")

  def onOpen(e: Event) = {
    console.log("Opened Websocket: ", e.`type`)
  }

  def onMessage(msg: MessageEvent) = {
    default.read[CodeEditorMessage](msg.data.toString) match {
      case msg: TextOperation => controller.operationFromRemote(msg)
      case msg: DocLoaded => controller.docLoadedMessage(msg)
      case msg: DocNotFound => controller.docNotFoundMessage(msg)
      case _ =>
    }
  }

  def sendMessage(msg: CodeEditorMessage) = ws.readyState match {
    case WebSocket.OPEN =>
      ws.send(default.write(msg))
    case _ =>
      console.error("Could not send Message because WebSocket is not in ready state!", ws)
  }
}
