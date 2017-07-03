package de.htwg.zeta.server.controller.restApi.metaModelUiFormat

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import play.api.libs.json.Reads
import play.api.libs.json.JsResult
import play.api.libs.json.Format
import play.api.libs.json.JsArray
import play.api.libs.json.Json
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.Writes


class MClassUiFormat(val enumMap: Map[String, MEnum]) extends Format[MClass] {

  private val attributeListReads: Reads[List[MAttribute]] = Reads.list(new MAttributeFormat(enumMap))

  override def reads(json: JsValue): JsResult[MClass] = {
    for {
      name <- json.\("name").validate[String](Reads.minLength[String](1))
      abstractness <- json.\("abstract").validate[Boolean]
      superTypes <- json.\("superTypes").validate[Seq[String]]
      inputs <- json.\("inputs").validate(Reads.list(MReferenceLinkDefFormat))
      outputs <- json.\("outputs").validate(Reads.list(MReferenceLinkDefFormat))
      attributes <- json.\("attributes").validate(attributeListReads)
    } yield {
      MClass(name, abstractness, superTypes, inputs, outputs, attributes)
    }
  }

  override def writes(o: MClass): JsValue = MClassUiFormat.writes(o)
}


object MClassUiFormat extends Writes[MClass] {
  override def writes(mc: MClass): JsValue = {
    Json.obj(
      "mType" -> "mClass",
      "name" -> mc.name,
      "abstract" -> mc.abstractness,
      "superTypes" -> JsArray(mc.superTypeNames.map(JsString)),
      "inputs" -> JsArray(mc.inputs.map(MReferenceLinkDefFormat.writes)),
      "outputs" -> JsArray(mc.outputs.map(MReferenceLinkDefFormat.writes)),
      "attributes" -> JsArray(mc.attributes.map(MAttributeFormat.writes))
    )
  }
}
