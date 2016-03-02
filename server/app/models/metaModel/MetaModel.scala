package models.metaModel

import models.metaModel.mCore.MObject
import models.metaModel.mCore.MCoreWrites._
import models.metaModel.mCore.MCoreReads._
import play.api.data.validation.ValidationError
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

case class Concept(
  elements: Map[String, MObject],
  uiState: String
)

object Concept {

  implicit val conceptReads: Reads[Concept] = (
    (__ \ "elements").read[Map[String, MObject]] and
      (__ \ "uiState").read[String]
    ) (Concept.apply _)

  implicit val conceptWrites = new Writes[Concept] {
    def writes(c: Concept): JsValue = Json.obj(
      "elements" -> Json.toJson(c.elements.values.toList),
      "uiState" -> c.uiState
    )
  }
}

case class MetaModel(
  name: String,
  concept: Concept,
  shape: Option[Shape],
  style: Option[Style],
  diagram: Option[Diagram]
)

object MetaModel {

  implicit val definitionReads = Json.reads[MetaModel].filter(
    ValidationError("Name must not be empty")
  ) {
    m => m.name.length > 0
  }

  implicit val definitionWrites = Json.writes[MetaModel]
}