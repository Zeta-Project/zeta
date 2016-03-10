package models.modelDefinitions.metaModel

import play.api.libs.json.Json

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

case class Dsl(
  diagram: Option[Diagram] = None,
  shape: Option[Shape] = None,
  style: Option[Style] = None
)

object Dsl {
  implicit val dslReads = Json.reads[Dsl]
  implicit val dslWrites = Json.writes[Dsl]
}