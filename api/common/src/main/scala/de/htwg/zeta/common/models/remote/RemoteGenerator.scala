package de.htwg.zeta.common.models.remote

import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketFactory
import com.neovisionaries.ws.client.WebSocketFrame
import de.htwg.zeta.common.models.frontend.FromGenerator
import de.htwg.zeta.common.models.frontend.GeneratorCompleted
import de.htwg.zeta.common.models.frontend.GeneratorResponse
import de.htwg.zeta.common.models.frontend.RunGeneratorFromGenerator
import de.htwg.zeta.common.models.frontend.StartGeneratorError
import de.htwg.zeta.common.models.frontend.ToGenerator
import play.api.libs.json.JsError
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import rx.lang.scala.Observable
import rx.lang.scala.Subject
import rx.lang.scala.Subscriber

object RemoteGenerator {
  def apply(session: String, work: String, parent: Option[String] = None, key: Option[String] = None): Remote = new RemoteGenerator(session, work, parent, key)
}

class RemoteGenerator(session: String, work: String, parent: Option[String] = None, key: Option[String] = None) extends Remote {
  val uri = s"ws://api:9000/socket/generator/${work}"
  val ws = new WebSocketFactory().createSocket(uri).addHeader("Cookie", s"SyncGatewaySession=${session};")

  // The sequence number which will be used on emit to the parent generator
  val sequence = new AtomicInteger(0)

  // A Subject is an Observable and an Observer at the same time.
  private val events = Subject[GeneratorResponse]()
  private val connection: Observable[GeneratorResponse] = events

  ws.addListener(new WebSocketAdapter() {
    // A text message arrived from the server.
    override def onTextMessage(ws: WebSocket, message: String) = {
      val json = Json.parse(message)

      json.validate[GeneratorResponse] match {
        case s: JsSuccess[GeneratorResponse] => {
          events.onNext(s.get)
        }
        case JsError(errors) => {
          events.onError(new Exception(s"Unable to parse server response on remote generator call : ${message}"))
        }
      }
    }

    override def onDisconnected(websocket: WebSocket, serverCloseFrame: WebSocketFrame, clientCloseFrame: WebSocketFrame, closedByServer: Boolean) = {
      if (closedByServer) {
        events.onError(new Exception(s"Connection for remote generator call was closed by server : ${serverCloseFrame.toString}"))
      } else {
        events.onError(new Exception(s"Connection for remote generator call was closed by client : ${clientCloseFrame.toString}"))
      }
    }
  })

  private def sendRunGenerator[Input](key: String, generatorId: UUID, options: Input)(implicit writes: Writes[Input]) = {
    val message = Json.toJson(options).toString()
    val json = Json.toJson(RunGeneratorFromGenerator(work, key, generatorId, message))
    ws.sendText(json.toString())
  }

  // Connect to the server and perform an opening handshake.
  // This method blocks until the opening handshake is finished.
  ws.connect()

  def call[Input, Output](generatorId: UUID, options: Input)(implicit writes: Writes[Input], reads: Reads[Output]): Observable[Output] = {
    val subscriptionKey = UUID.randomUUID().toString

    // start the generator with the provided options
    sendRunGenerator(subscriptionKey, generatorId, options)

    Observable[Output](subscriber => {
      val i = new AtomicInteger(0)

      connection.subscribe(response => {
        response match {
          case StartGeneratorError(key, reason) =>
            processStartError(subscriptionKey, subscriber, key, reason)
          case FromGenerator(index, key, message) => if (key == subscriptionKey) {
            if (index == i.incrementAndGet()) {
              Json.parse(message).validate[Output] match {
                case JsSuccess(value, path) =>
                  processMessageSuccess(subscriber, value)
                case JsError(errors) =>
                  createError(subscriber, generatorId, options.toString, s"Unable to parse message '${message}' from generator to expected Output format.")
              }
            } else {
              createError(subscriber, generatorId, options.toString, "Sequence number was not as expected. Lost messages.")
            }
          }
          case GeneratorCompleted(key, result) => if (key == subscriptionKey) {
            processComplete(generatorId, options, subscriber, result)
          }
        }
      })
    })
  }

  private def processStartError[Output](subscriptionKey: String, subscriber: Subscriber[Output], key: String, reason: String) = {
    if (key == subscriptionKey) {
      subscriber.onError(new Exception(reason))
    }
  }

  private def createError[Output](subscriber: Subscriber[Output], generatorId: UUID, options: String, message: String) = {
    subscriber.onError(new Exception(s"Remote generator call of $generatorId with options $options fired an error : $message"))
  }

  private def processMessageSuccess[Output](subscriber: Subscriber[Output], value: Output) = {
    if (!subscriber.isUnsubscribed) {
      subscriber.onNext(value)
    }
  }

  private def processComplete[Input, Output](generatorId: UUID, options: Input, subscriber: Subscriber[Output], result: Int) = {
    if (result == 0) {
      subscriber.onCompleted()
    } else {
      createError(subscriber, generatorId, options.toString, s"Generator completed with status code ${result}")
    }
  }

  def emit[Output](value: Output)(implicit writes: Writes[Output]): Unit = parent match {
    case Some(parent) => key match {
      case Some(key) => {
        val message = Json.toJson(value).toString()
        val json = Json.toJson(ToGenerator(sequence.incrementAndGet(), key, parent, message)).toString
        ws.sendText(json)
      }
      case None => new Exception("Unable to publish because this generator was not called by another generator. Missing key parameter.")
    }
    case None => new Exception("Unable to publish because this generator was not called by another generator. Missing parent parameter.")
  }
}
