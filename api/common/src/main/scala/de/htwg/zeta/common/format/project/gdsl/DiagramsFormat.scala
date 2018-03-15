package de.htwg.zeta.common.format.project.gdsl

import de.htwg.zeta.common.format.project.gdsl.diagram.DiagramFormat
import de.htwg.zeta.common.models.project.gdsl.diagram.Diagram
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class DiagramsFormat(
    diagramFormat: DiagramFormat,
    sDiagrams: String = "diagrams"
) extends OFormat[List[Diagram]] {

  override def writes(clazz: List[Diagram]): JsObject = Json.obj(
    sDiagrams -> Writes.list(diagramFormat).writes(clazz)
  )

  override def reads(json: JsValue): JsResult[List[Diagram]] =
    (json \ sDiagrams).validate(Reads.list(diagramFormat))

}
