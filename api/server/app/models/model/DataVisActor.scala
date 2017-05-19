package models.model

import java.io.File

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import models.model.DataVisActor.MetamodelFailure
import models.model.DataVisActor.MetamodelLoaded
import models.model.ModelWsActor.DataVisInvalidError
import play.api.Logger
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import shared.DiagramWSMessage.DataVisCodeMessage
import shared.DiagramWSMessage.DataVisScopeQuery
import de.htwg.zeta.server.util.MetamodelBuilder
import de.htwg.zeta.server.util.datavis.domain.Conditional
import de.htwg.zeta.server.util.datavis.generator.ListenersGenerator
import de.htwg.zeta.server.util.datavis.parser.DataVisParsers
import de.htwg.zeta.server.util.datavis.validator.ConstrainedDataVisValidator
import de.htwg.zeta.server.util.domain.Metamodel
import de.htwg.zeta.server.util.domain.ObjectWithAttributes

class DataVisActor(socket: ActorRef, instanceId: String, graphType: String) extends Actor with DataVisParsers {
  val log = Logger(this getClass () getName ())
  var metamodel: Metamodel = null
  val generator = new ListenersGenerator

  // TODO: Connect model instance to new REST API

  override def receive = {
    case msg: DataVisCodeMessage => handleDataVisCode(msg)
    case DataVisScopeQuery(mClass) => handleScopeQuery(mClass)
    case MetamodelLoaded(code) => metamodel = MetamodelBuilder().fromJson(Json.parse(code).asInstanceOf[JsObject])
    case MetamodelFailure() => log.error("Unable to lead metamodel")
    case _ => log.error("Unknown message received")
  }

  private def handleDataVisCode(msg: DataVisCodeMessage) = {
    log.debug("DataVis Code for object " + msg.context + ": " + msg.code)

    metamodel.getObjectByName(msg.classname) match {
      case None => socket ! ModelWsActor.DataVisInvalidError(List("Unable to load class " + msg.classname + " from metamodel."), msg.context)
      case Some(mObject) => parseAll(script, msg.code) match {
        case NoSuccess(error, _) => socket ! ModelWsActor.DataVisParseError("Could not parse code: " + error, msg.context)
        case Success(conditionals, _) => validateAndGenerateDataVisCode(mObject, conditionals, msg)
      }
    }
  }

  private def handleScopeQuery(classname: String) = {}

  private def validateAndGenerateDataVisCode(mObject: ObjectWithAttributes, conditionals: List[Conditional], msg: DataVisCodeMessage) = {
    val validator = new ConstrainedDataVisValidator
    if (validator.validate(conditionals, mObject)) {
      generateAndPublish(msg, conditionals)
    } else {
      socket ! DataVisInvalidError(validator.errors.toList, msg.context)
    }
  }

  private def generateAndPublish(msg: DataVisCodeMessage, conditionals: List[Conditional]) = {
    val fileName = generator.generate(instanceId, msg.context, conditionals)
    socket ! ModelWsActor.PublishFile(msg.context, ("/assets/" + fileName).replace(File.separator, "/"))
  }
}

object DataVisActor {
  def props(socket: ActorRef, instanceId: String, graphType: String) = Props(new DataVisActor(socket, instanceId, graphType))
  case class MetamodelLoaded(json: String)
  case class MetamodelFailure()
}
