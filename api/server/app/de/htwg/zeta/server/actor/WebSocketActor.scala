package de.htwg.zeta.server.actor

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import de.htwg.zeta.server.actor.OnlineUserActor.AreaState
import de.htwg.zeta.server.actor.OnlineUserActor.ClientInfo
import de.htwg.zeta.server.actor.OnlineUserActor.ClientOffline
import de.htwg.zeta.server.actor.OnlineUserActor.ClientOnline
import de.htwg.zeta.server.actor.WebSocketActor.WebSocketEvent
import de.htwg.zeta.server.silhouette.ZetaIdentity
import grizzled.slf4j.Logging
import play.api.libs.json.JsError
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Writes

object WebSocketActor {
  case class WebSocketEvent[T](name: String, body: T)

  def props(user: ZetaIdentity, onlineUserManagerFactory: OnlineUserActorFactory)(out: ActorRef): Props =
    Props(new WebSocketActor(user, out, onlineUserManagerFactory.onlineActor))
}

class WebSocketActor(user: ZetaIdentity, out: ActorRef, manager: ActorRef) extends Actor with Logging {
  var area: Option[String] = None

  override def postStop(): Unit = {
    super.postStop()
    area.foreach(a => manager ! ClientOffline(user, a))
  }

  def receive: PartialFunction[Any, Unit] = {
    case s: AreaState => out ! Json.toJson(s).toString
    case s: String => handleEvents(s)
  }

  private def handleEvents(s: String): Unit = {
    jsonEvent(Json.parse(s)) match {
      case JsSuccess(value, _) if value.name == "online" =>
        area = Some(value.body)
        manager ! ClientOnline(user, value.body)
      case JsSuccess(value, _) if value.name == "offline" =>
        manager ! ClientOffline(user, value.body)

      case JsSuccess(value, _) => logger.info(s"Unknown event: ${value.name}")
      case JsError(e) => logger.error(e)
    }
  }

  private def jsonEvent(json: JsValue): JsResult[WebSocketEvent[String]] = for {
    name <- (json \ "name").validate[String]
    value <- (json \ "value").validate[String]
  } yield {
    WebSocketEvent(name, value)
  }

  private implicit lazy val stateToJson: Writes[AreaState] =
    (o: AreaState) => Json.obj(
      "onlineClients" -> Writes.seq(clientInfoToJson).writes(o.onlineClients)
    )

  private implicit lazy val clientInfoToJson: Writes[ClientInfo] =
    (o: ClientInfo) => Json.obj(
      "fullName" -> o.fullName
    )
}
