package models

import java.io.File

import akka.actor.{Actor, ActorRef, Props}
import models.DiagramWSActor.DataVisInvalidError
import modigen.util.datavis.domain.Conditional
import modigen.util.datavis.dummy.Dummy
import modigen.util.datavis.generator.ListenersGenerator
import modigen.util.datavis.parser.DataVisParsers
import modigen.util.datavis.validator.ConstrainedDataVisValidator
import modigen.util.domain.{ObjectWithAttributes, MReference, MClass}
import play.api.Logger
import shared.DiagramWSMessage.{DataVisScopeQuery, DataVisCodeMessage}
import shared.DiagramWSOutMessage.DataVisScope

class DataVisActor(socket:ActorRef, instanceId:String) extends Actor with DataVisParsers{
  val log = Logger(this getClass() getName())
  val metamodel = Dummy.metamodel(instanceId)
  val generator = new ListenersGenerator

  override def receive = {
    case msg:DataVisCodeMessage => handleDataVisCode(msg)
    case DataVisScopeQuery(mClass) => handleScopeQuery(mClass)
    case _ => log.error("Unknown message received")
  }

  private def handleDataVisCode(msg:DataVisCodeMessage) = {
    log.debug("DataVis Code for object " + msg.context + ": " + msg.code)

    metamodel.getObjectByName(msg.classname) match {
      case None => socket ! DiagramWSActor.DataVisInvalidError(List("Unable to load class " + msg.classname + " from metamodel."), msg.context)
      case Some(mObject) => parseAll(script, msg.code) match {
        case NoSuccess(error, _) => socket ! DiagramWSActor.DataVisParseError("Could not parse code: " + error, msg.context)
        case Success(conditionals, _) => validateAndGenerateDataVisCode(mObject, conditionals, msg)
      }
    }
  }

  private def handleScopeQuery(classname:String) = {
    log.debug("Scope query for MObj" + classname)
    metamodel.getObjectByName(classname) match {
      case None => socket ! DiagramWSActor.DataVisInvalidError(List("Unable to load class " + classname + " from metamodel."), "")
      case Some(mObject) => mObject match {
        case mClass:MClass => socket ! DataVisScope(mClass.allAttributes.map(_.name), classname)
        case mReference:MReference => socket ! DataVisScope(mReference.attributes.map(_.name), classname)
      }
    }
  }

  private def  validateAndGenerateDataVisCode(mObject:ObjectWithAttributes, conditionals:List[Conditional], msg:DataVisCodeMessage) = {
    val validator = new ConstrainedDataVisValidator
    if (validator.validate(conditionals, mObject))
      generateAndPublish(msg, conditionals)
    else
      socket ! DataVisInvalidError(validator.errors.toList, msg.context)
  }

  private def generateAndPublish(msg:DataVisCodeMessage, conditionals:List[Conditional]) = {
    val fileName = generator.generate(instanceId, msg.context, conditionals)
    socket ! DiagramWSActor.PublishFile(msg.context,  ("/assets/" + fileName).replace(File.separator, "/"))
  }
}

object DataVisActor{
  def props(socket:ActorRef, instanceId:String) = Props(new DataVisActor(socket, instanceId))
}