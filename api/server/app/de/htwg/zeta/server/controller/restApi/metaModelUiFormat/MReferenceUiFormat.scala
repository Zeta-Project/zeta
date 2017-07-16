package de.htwg.zeta.server.controller.restApi.metaModelUiFormat

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method
import play.api.libs.json.Reads
import play.api.libs.json.JsResult
import play.api.libs.json.Format
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.Writes
import play.api.libs.json.JsArray

class MReferenceUiFormat(val enumMap: Map[String, MEnum]) extends Format[MReference] {

  private val attributeListReads: Reads[List[MAttribute]] = Reads.list(new MAttributeFormat(enumMap))

  override def reads(json: JsValue): JsResult[MReference] = {
    for {
      name <- json.\("name").validate[String](Reads.minLength[String](1))
      description <- json.\("description").validate[String]
      sourceDeletionDeletesTarget <- json.\("sourceDeletionDeletesTarget").validate[Boolean]
      targetDeletionDeletesSource <- json.\("targetDeletionDeletesSource").validate[Boolean]
      source <- json.\("source").validate(Reads.list(MClassLinkDefFormat))
      target <- json.\("target").validate(Reads.list(MClassLinkDefFormat))
      attributes <- json.\("attributes").validate(attributeListReads)
      methods <- json.\("methods").validate(Reads.list(Method.playJsonFormat))
    } yield {
      MReference(name, description, sourceDeletionDeletesTarget, targetDeletionDeletesSource, source, target, attributes, methods)
    }
  }

  override def writes(o: MReference): JsValue = MReferenceUiFormat.writes(o)
}


object MReferenceUiFormat extends Writes[MReference] {
  override def writes(mr: MReference): JsValue = {
    Json.obj(
      "mType" -> "mReference",
      "name" -> mr.name,
      "sourceDeletionDeletesTarget" -> mr.sourceDeletionDeletesTarget,
      "targetDeletionDeletesSource" -> mr.targetDeletionDeletesSource,
      "source" -> JsArray(mr.source.map(MClassLinkDefFormat.writes)),
      "target" -> JsArray(mr.target.map(MClassLinkDefFormat.writes)),
      "attributes" -> JsArray(mr.attributes.map(MAttributeFormat.writes)),
      "methods" -> JsArray(mr.methods.map(Method.playJsonFormat.writes))
    )
  }
}
