package controller

import org.scalajs.dom
import org.scalajs.dom.{CloseEvent, ErrorEvent, Event, MessageEvent, WebSocket, console}
import shared.CodeEditorMessage
import shared.CodeEditorMessage.TextOperation
import upickle.default._

import scala.collection.mutable
import org.scalajs.jquery._
import scalatags.Text.all._

case class WebSocketConnection(uri: String = "ws://141.37.112.195:9000/socket", controller: CodeEditorController) {

  /** Set up WebSocket connection */
  val ws = new dom.WebSocket(uri)
  ws.onmessage = (msg: MessageEvent) => onMessage(msg)
  ws.onopen = (e: Event) => onOpen(e)
  ws.onerror = (e: ErrorEvent) => console.error(s"Websocket Error! ${e.message}")
  ws.onclose = (e: CloseEvent) => console.log(s"Closed WS for Reason: ${e.reason}")

  var sendBuffer = mutable.Queue[CodeEditorMessage]()
  var receiveBuffer = mutable.Queue[CodeEditorMessage]()

  def onOpen(e: Event) = {
    console.log("Opened Websocket: ", e.toString)
  }

  def addToBuffer(msg: CodeEditorMessage) = {
    println("Adding to send buffer!")
    sendBuffer.enqueue(msg)
    updateSendBufferView()
  }

  def sendFromBuffer() = {
    sendBuffer.isEmpty match {
      case false =>
        sendMessage(sendBuffer.dequeue())
        updateSendBufferView()
      case _ => println("Couldn't send, buffer empty!")
    }
  }

  def receiveFromBuffer() = {
    receiveBuffer.isEmpty match {
      case false =>
        controller.operationFromRemote(receiveBuffer.dequeue().asInstanceOf[TextOperation].op)
        updateReceiveBufferView()
        updateSendBufferView()
      case _ => println("Couldn't apply, buffer empty!")
    }
  }

  def updateReceiveBufferView() = {
    jQuery("#receiveBuffer").html(
      ul(`class` := "list-group")(
        for (x <- receiveBuffer.toArray) yield {
          li(`class` := "list-group-item")(
            x.asInstanceOf[TextOperation].op.toString()
          )
        }
      ).render
    )
  }

  def updateSendBufferView() = {
    jQuery("#sendBuffer").html(
      span(
      for (x <- sendBuffer.toArray) yield {
          li(`class` := "list-group-item")(
            x.asInstanceOf[TextOperation].op.toString()
          )
        }
      ).render
    )
  }

  def onMessage(msg: MessageEvent) = {
    read[CodeEditorMessage](msg.data.toString) match {

      case msg: TextOperation =>
        println("Adding to received buffer!")
        receiveBuffer.enqueue(msg)
        updateReceiveBufferView()

      case _ => // console.error("Unknown message received from Server!")
    }
  }

  def sendMessage(msg: CodeEditorMessage) = ws.readyState match {
    case WebSocket.OPEN =>
      ws.send(write(msg))
    case _ =>
      console.error("Could not send Message because WebSocket is not in ready state!", ws)
  }
}
