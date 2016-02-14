package models.metaModel

import models.metaModel.mCore.MObject

import models.metaModel.mCore.MCoreWrites._
import models.metaModel.mCore.MCoreReads._

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._

import scala.collection.immutable._

case class Style(code: String)

object Style {
  implicit val styleReads = Json.reads[Style]
  implicit val styleWrites = Json.writes[Style]
}

case class Shape(code: String)

object Shape {
  implicit val shapeReads = Json.reads[Shape]
  implicit val shapeWrites = Json.writes[Shape]
}

case class Diagram(code: String)

object Diagram {
  implicit val diagramReads = Json.reads[Diagram]
  implicit val diagramWrites = Json.writes[Diagram]
}

case class Definition(
  name: String,
  mObjects: Map[String, MObject],
  graph: String
)

object Definition {

  implicit val definitionReads: Reads[Definition] = (
    (__ \ "name").read[String] and
      (__ \ "mObjects").read[Map[String, MObject]] and
      (__ \ "graph").read[String]
    ) (Definition.apply _)

  implicit val definitionWrites = new Writes[Definition] {
    def writes(d: Definition): JsValue = Json.obj(
      "name" -> d.name,
      "mObjects" -> Json.toJson(d.mObjects.values.toList),
      "graph" -> d.graph
    )
  }
}

case class MetaModel(
  id: Option[String],
  userId: String,
  definition: Definition,
  style: Style,
  shape: Shape,
  diagram: Diagram
)

object MetaModel {
  implicit val metaModelReads = Json.reads[MetaModel]
  implicit val metaModelWrites = Json.writes[MetaModel]
}



