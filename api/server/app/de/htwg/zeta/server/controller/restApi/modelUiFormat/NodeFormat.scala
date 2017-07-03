package de.htwg.zeta.server.controller.restApi.modelUiFormat

import scala.collection.immutable.Seq
import scala.collection.immutable.List

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToEdges
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

  private def extractEdges(typeHas: String => Boolean)(m: Map[String, Seq[String]]): JsResult[List[ToEdges]] = {
    m.toList.reverse.foldLeft[JsResult[List[ToEdges]]](JsSuccess(Nil))((res, kv) => {
      res match {
        case JsSuccess(list, _) =>
          val (k, v) = kv
          metaModel.referenceMap.get(k) match {
            case Some(t: MReference) if typeHas(t.name) => JsSuccess(ToEdges(t, v) :: list)
            case None => invalidToEdgesError
          }
        case e: JsError => e
      }
    })
  }

  override def reads(json: JsValue): JsResult[Node] = {
    for {
      name <- (json \ "id").validate[String]
      clazz <- (json \ "mClass").validate[String].flatMap(className => metaModel.classMap.get(className) match {
        case Some(mClass) => JsSuccess(mClass)
        case None => unknownMClassError
      })
      traverse = MClass.MClassTraverseWrapper(clazz, MetaModel.MetaModelTraverseWrapper(metaModel))
      output <- (json \ "outputs").validate[Map[String, Seq[String]]].flatMap(extractEdges(traverse.typeHasOutput))
      input <- (json \ "inputs").validate[Map[String, Seq[String]]].flatMap(extractEdges(traverse.typeHasInput))
      attr <- (json \ "attributes").validate(AttributeFormat(clazz.attributes, name))

    } yield {
      Node(name, clazz, output, input, attr)
    }
  }

  override def writes(o: Node): JsValue = NodeFormat.writes(o)

}

object NodeFormat extends Writes[Node] {

  def apply(metaModel: MetaModel): NodeFormat = new NodeFormat(metaModel)

  private def writeEdges(seq: Seq[ToEdges]): Map[String, Seq[String]] = {
    seq.map(te => (te.reference.name, te.edgeNames)).toMap
  }

  override def writes(o: Node): JsValue = {
    Json.obj(
      "id" -> o.name,
      "mClass" -> o.clazz.name,
      "outputs" -> writeEdges(o.outputs),
      "inputs" -> writeEdges(o.inputs),
      "attributes" -> AttributeFormat.writes(o.attributes)
    )
  }

}
