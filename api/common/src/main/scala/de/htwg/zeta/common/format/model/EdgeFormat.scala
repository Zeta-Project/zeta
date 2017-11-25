package de.htwg.zeta.common.format.model

import scala.collection.immutable.List
import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClassLinkDef
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.NodeLink
import play.api.libs.json.Format
import play.api.libs.json.JsError
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Writes


class EdgeFormat private(metaModel: MetaModel) extends Format[Edge] {
  val invalidToNodesError = JsError("node reference has invalid type 2")
  val unknownMReferenceError = JsError("Unknown mReference")


  private def extractToNodes(classLinks: Seq[MClassLinkDef])(m: Map[String, Seq[String]]): JsResult[List[NodeLink]] = {
    m.toList.reverse.foldLeft[JsResult[List[NodeLink]]](JsSuccess(Nil))((res, kv) => {
      res match {
        case JsSuccess(list, _) =>
          val (k, v) = kv
          metaModel.classMap.get(k) match {
            case Some(t: MClass) if classLinks.exists(mLink => mLink.className == t.name) =>
              JsSuccess(NodeLink(t.name, v) :: list)
            case None => invalidToNodesError
          }
        case e: JsError => e
      }
    })
  }

  override def reads(json: JsValue): JsResult[Edge] = {
    for {
      name <- json.\("id").validate[String]
      mReference <- json.\("mReference").validate[String]
      // Todo not sure if check is correct or needs to be swapped
      source <- json.\("source").validate[Map[String, Seq[String]]].map(_.map(e => NodeLink(e._1, e._2)))
      target <- json.\("target").validate[Map[String, Seq[String]]].map(_.map(e => NodeLink(e._1, e._2)))
      // attributes <- json.\("attributes").validate(AttributeFormat(mReference.attributes, name)) // TODO FIXME
    } yield {
      Edge(name, mReference, source.toList, target.toList, Seq.empty, Map.empty, Seq.empty)
    }
  }

  override def writes(o: Edge): JsValue = EdgeFormat.writes(o)

}

object EdgeFormat extends Writes[Edge] {

  def apply(metaModel: MetaModel): EdgeFormat = new EdgeFormat(metaModel)

  private def writeNodes(seq: Seq[NodeLink]): Map[String, Seq[String]] = {
    seq.map(tn => (tn.className, tn.nodeNames)).toMap
  }

  override def writes(o: Edge): JsValue = {
    Json.obj(
      "id" -> o.name,
      "mReference" -> o.referenceName,
      "source" -> writeNodes(o.source),
      "target" -> writeNodes(o.target),
      "attributes" -> AttributeFormat.writes(o.attributeValues)
    )
  }
}
