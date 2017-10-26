package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import scala.collection.immutable.Seq
import scala.collection.immutable.SortedMap

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes


case class Method(
    name: String,
    parameters: SortedMap[String, AttributeType],
    description: String,
    returnType: AttributeType,
    code: String
)

object Method {

  trait MethodMap {

    val methods: Seq[Method]

    /** Methods mapped to their own names. */
    final val methodMap: Map[String, Method] = methods.map(method => (method.name, method)).toMap

  }

  private val sName = "name"

  def playJsonReads(enums: Seq[MEnum]): Reads[Method] = Reads { json =>
    for {
      name <- (json \ sName).validate[String]
      parameters <- (json \ "parameters").validate(playJsonReadsParameterMap(enums))
      description <- (json \ "description").validate[String]
      returnType <- (json \ "returnType").validate(AttributeType.playJsonReads(enums))
      code <- (json \ "code").validate[String]
    } yield {
      Method(name, parameters, description, returnType, code)
    }
  }

  private def playJsonReadsParameterMap(enums: Seq[MEnum]): Reads[SortedMap[String, AttributeType]] = Reads { json =>
    json.validate(Reads.list(playJsonReadsParameter(enums))).map(SortedMap(_: _*))
  }

  private def playJsonReadsParameter(enums: Seq[MEnum]): Reads[(String, AttributeType)] = Reads { json =>
    for {
      name <- (json \ sName).validate[String]
      typ <- (json \ "typ").validate(AttributeType.playJsonReads(enums))
    } yield {
      (name, typ)
    }
  }

  implicit val playJsonWrites: Writes[Method] = Json.writes[Method]

}
