package de.htwg.zeta.server.controller.restApi.modelUiFormat

import java.util.UUID

import scala.collection.immutable.Seq
import scala.collection.immutable.List

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.EdgeLink
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsResult
import play.api.libs.json.Format
import play.api.libs.json.JsError
import play.api.libs.json.Writes
import play.api.libs.json.JsValue
import play.api.libs.json.Json


class NodeFormat private(metaModel: MetaModel) extends Format[Node] {
  private val unknownMClassError = JsError("Unknown mClass")
  private val invalidToEdgesError = JsError("edge reference has invalid type")

  private def extractEdges(typeHas: String => Boolean)(m: Map[String, Seq[String]]): JsResult[List[EdgeLink]] = {
    m.toList.reverse.foldLeft[JsResult[List[EdgeLink]]](JsSuccess(Nil))((res, kv) => {
      res match {
        case JsSuccess(list, _) =>
          val (k, v) = kv
          metaModel.referenceMap.get(k) match {
            case Some(t: MReference) if typeHas(t.name) => JsSuccess(EdgeLink(t.name, v) :: list)
            case None => invalidToEdgesError
          }
        case e: JsError => e
      }
    })
  }

  override def reads(json: JsValue): JsResult[Node] = {
    for {
      id <- (json \ "id").validate[String]
      clazz <- (json \ "mClass").validate[String].flatMap(className => metaModel.classMap.get(className) match {
        case Some(mClass) => JsSuccess(mClass)
        case None => unknownMClassError
      })
      traverse = MClass.MClassTraverseWrapper(clazz, MetaModel.MetaModelTraverseWrapper(metaModel))
      output <- (json \ "outputs").validate[Map[String, Seq[String]]].flatMap(extractEdges(traverse.typeHasOutput))
      input <- (json \ "inputs").validate[Map[String, Seq[String]]].flatMap(extractEdges(traverse.typeHasInput))
      attr <- (json \ "attributes").validate(AttributeFormat(clazz.attributes, id.toString))

    } yield {
      Node(id, clazz.name, output, input, Seq.empty, attr, Seq.empty)
    }
  }

  override def writes(o: Node): JsValue = NodeFormat.writes(o)

}

object NodeFormat extends Writes[Node] {

  def apply(metaModel: MetaModel): NodeFormat = new NodeFormat(metaModel)

  private def writeEdges(seq: Seq[EdgeLink]): Map[String, Seq[String]] = {
    seq.map(te => (te.referenceName, te.edgeNames)).toMap
  }

  override def writes(o: Node): JsValue = {
    Json.obj(
      "name" -> o.name,
      "mClass" -> o.className,
      "outputs" -> writeEdges(o.outputs),
      "inputs" -> writeEdges(o.inputs),
      "attributes" -> AttributeFormat.writes(o.attributeValues)
    )
  }

}
