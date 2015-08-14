package models

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator.{Publish, Subscribe}
import shared.CodeEditorMessage
import shared.CodeEditorMessage.TextOperation
import upickle.default._

object SingleDoc{
  val doc = scalot.Server("")
}
case class MediatorMessage(msg: Any, broadcaster: ActorRef)

class CodeWSActor(out: ActorRef) extends Actor with ActorLogging {

  val mediator = DistributedPubSubExtension(context.system).mediator
  val uuid = UUID.randomUUID().toString
  mediator ! Subscribe("content", self)


  def receive = {
    case pickled: String => try {
      read[CodeEditorMessage](pickled) match {
        case msg: TextOperation =>
          SingleDoc.doc.receiveOperation(msg.op) match {
            case Some(op) => mediator ! Publish("content",MediatorMessage(write(TextOperation(op)),self))
            case _ => // Nothing to do!
          }
          println("Doc is"+SingleDoc.doc.str)

        case _ => println("Disarding message, probably sent by myself")
      }
    }
    case medMsg : MediatorMessage =>
      out ! medMsg.msg

    case _ => println(s" ${self.toString()} - Message is not a String!")
  }

  override def preStart() = {
    println("Started Actor!")
  }
}

object CodeWSActor {
  def props(out: ActorRef) = Props(new CodeWSActor(out))
}

