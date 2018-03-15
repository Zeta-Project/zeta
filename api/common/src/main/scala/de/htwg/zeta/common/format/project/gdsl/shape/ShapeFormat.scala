package de.htwg.zeta.common.format.project.gdsl.shape

import de.htwg.zeta.common.models.project.gdsl.shape.Shape
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class ShapeFormat(
    nodeFormat: NodeFormat,
    edgeFormat: EdgeFormat,
    sNodes: String = "nodes",
    sEdges: String = "edges"
) extends OFormat[Shape] {

  override def writes(clazz: Shape): JsObject = Json.obj(
    sNodes -> Writes.list(nodeFormat).writes(clazz.nodes),
    sEdges -> Writes.list(edgeFormat).writes(clazz.edges)
  )

  override def reads(json: JsValue): JsResult[Shape] = for {
    nodes <- (json \ sNodes).validate(Reads.list(nodeFormat))
    edges <- (json \ sEdges).validate(Reads.list(edgeFormat))
  } yield {
    Shape(nodes, edges)
  }
}
