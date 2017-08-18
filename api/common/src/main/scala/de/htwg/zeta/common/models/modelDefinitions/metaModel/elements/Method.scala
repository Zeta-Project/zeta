package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import scala.collection.immutable.Seq
import scala.collection.immutable.SortedMap

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
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

  trait HasMethods {

    val methods: Seq[Method]

    /** Methods mapped to their own names. */
    final val methodMap: Map[String, Method] = methods.map(method => (method.name, method)).toMap

  }

  private val sName = "name"


  def playJsonReads(enums: Seq[MEnum]): Reads[Method] = new Reads[Method] {

    // TODO implement sortedMap serializer for parameters
    override def reads(json: JsValue): JsResult[Method] = {
      for {
        name <- (json \ sName).validate[String]
        parameters <- (json \ "parameters").validate(Reads.map(AttributeType.playJsonReads(enums))) // TODO this is not sorted
        description <- (json \ "description").validate[String]
        returnType <- (json \ "returnType").validate(AttributeType.playJsonReads(enums))
        code <- (json \ "code").validate[String]
      } yield {
        Method(name, SortedMap(/* TODO */ parameters.toArray: _*), description, returnType, code)
      }
    }
  }

  implicit val playJsonWrites: Writes[Method] = Json.writes[Method]

}
