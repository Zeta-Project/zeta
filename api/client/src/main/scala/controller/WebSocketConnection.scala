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

class WebSocketConnection(
    uri: String = s"ws://${window.location.host}/codeEditor/socket",
    controller: CodeEditorController,
    metaModelId: UUID,
    dslType: String) {

  /** Set up WebSocket connection */
  val ws = new dom.WebSocket(s"$uri/${metaModelId.toString}/$dslType") // scalastyle:ignore multiple.string.literals
  ws.onmessage = (msg: MessageEvent) => onMessage(msg)
  ws.onopen = (e: Event) => onOpen(e)
  ws.onerror = (e: ErrorEvent) => console.error(s"WebSocket error! ${e.message}")
  ws.onclose = (e: CloseEvent) => console.log(s"Closed WebSocket for reason: ${e.reason}")

  def onOpen(e: Event): Unit = {
    console.log("Opened WebSocket: ", e.`type`)
  }

  def onMessage(msg: MessageEvent): Unit = {
    default.read[CodeEditorMessage](msg.data.toString) match {
      case msg: TextOperation => controller.operationFromRemote(msg)
      case msg: DocLoaded => controller.docLoadedMessage(msg)
      case msg: DocNotFound => controller.docNotFoundMessage(msg)
      case _ =>
    }
  }

  def sendMessage(msg: CodeEditorMessage): Unit = ws.readyState match {
    case WebSocket.OPEN =>
      ws.send(default.write(msg))
    case _ =>
      console.error("Could not send message because WebSocket is not in ready state!", ws)
  }

}
