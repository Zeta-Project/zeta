package models.metaModel


import play.api.data.validation.ValidationError
import play.api.libs.json._


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