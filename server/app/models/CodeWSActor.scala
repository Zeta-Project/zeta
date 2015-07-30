package models

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator.{Publish, Subscribe}
import shared.WebSocketMessages.{DocsAdded, GetDocs, DocChanged}
import shared._
import upickle.default._


object CodeWSActor {
  def props(out: ActorRef) = Props(new CodeWSActor(out))
}

case class MediatorMessage(msg: Any, broadcaster: ActorRef)

class CodeWSActor(out: ActorRef) extends Actor with ActorLogging {

  val mediator = DistributedPubSubExtension(context.system).mediator
  val uuid = UUID.randomUUID().toString
  mediator ! Subscribe("content", self)

  var docs: Seq[WootDoc] = Seq[WootDoc]()

  def receive = {
    case pickled: String => try {
      read[CodeEditorMessage](pickled) match {

        case msg: DocChanged =>
          mediator ! Publish("content", MediatorMessage(msg, self))

        case GetDocs =>
          out ! write(DocsAdded(docs))

        case msg: DocsAdded =>
          mediator ! Publish("content", MediatorMessage(msg, self))

        case _ =>
          println(s" ${self.toString()} - Unknown Type of unpickled message!")
      }
    } catch {
      case e: upickle.Invalid =>
        println(e.getMessage)
    }

    /** Mediator Routed Messages */
    case MediatorMessage(any, broadcaster) =>
      any match {
        case msg: DocChanged =>
          msg.ops.foreach((x) => {
            docs.filter(_.uuid == msg.doc.uuid)
              .foreach((y) => {
              y.woot = y.woot.integrate(x)._2
              println(s" ${self.toString()} - $uuid DocChangedMessage -- Content:${y.woot.text}")
            })
          })
          if (broadcaster != self)
            out ! write(msg)

        case msg: DocsAdded =>
          msg.docs.foreach(x => docs :+= x.copy(woot = x.woot.copy(new SiteId(uuid))))
          if (broadcaster != self)
            out ! write(msg)

        case _ => println("Discarding message, probably sent by myself")

      }

    case _ => println(s" ${self.toString()} - Message is not a String!")
  }

  override def preStart() = {
    println("Started Actor!")
  }
}
