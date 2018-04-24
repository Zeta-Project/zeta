package de.htwg.zeta.common.format.project.gdsl.shape

import de.htwg.zeta.common.format.project.gdsl.shape.geoModel.GeoModelFormat
import de.htwg.zeta.common.format.project.gdsl.style.StyleFormat
import de.htwg.zeta.common.models.project.gdsl.shape.Node
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

class NodeFormat(
    styleFormat: StyleFormat,
    edgeFormat: EdgeFormat,
    sizeFormat: SizeFormat,
    resizingFormat: ResizingFormat,
    geoModelFormat: GeoModelFormat,
    sName: String,
    sConceptElement: String,
    sEdges: String,
    sSize: String,
    sStyle: String,
    sResizing: String,
    sGeoModels: String
) extends OFormat[Node] {

  override def writes(clazz: Node): JsObject = Json.obj(
    sName -> clazz.name,
    sConceptElement -> clazz.conceptElement,
    sEdges -> Writes.list(edgeFormat).writes(clazz.edges),
    sSize -> sizeFormat.writes(clazz.size),
    sStyle -> styleFormat.writes(clazz.style),
    sResizing -> resizingFormat.writes(clazz.resizing),
    sGeoModels -> Writes.list(geoModelFormat).writes(clazz.geoModels)
  )

  override def reads(json: JsValue): JsResult[Node] = for {
    name <- (json \ sName).validate[String]
    conceptElement <- (json \ sConceptElement).validate[String]
    edges <- (json \ sEdges).validate(Reads.list(edgeFormat))
    size <- (json \ sSize).validate(sizeFormat)
    style <- (json \ sStyle).validate(styleFormat)
    resizing <- (json \ sResizing).validate(resizingFormat)
    geoModels <- (json \ sGeoModels).validate(Reads.list(geoModelFormat))
  } yield {
    Node(
      name,
      conceptElement,
      edges,
      size,
      style,
      resizing,
      geoModels
    )
  }

}
object NodeFormat {
  def apply(): NodeFormat = new NodeFormat(
    StyleFormat(),
    EdgeFormat(),
    SizeFormat(),
    ResizingFormat(),
    GeoModelFormat(),
    "name",
    "conceptElement",
    "edges",
    "size",
    "style",
    "resizing",
    "geoElements"
  )
}
