package de.htwg.zeta.common.format.metaModel

import scala.collection.immutable.Seq

import de.htwg.zeta.common.format.metaModel.MClassFormat.sAbstractness
import de.htwg.zeta.common.format.metaModel.MClassFormat.sAttributes
import de.htwg.zeta.common.format.metaModel.MClassFormat.sDescription
import de.htwg.zeta.common.format.metaModel.MClassFormat.sInputs
import de.htwg.zeta.common.format.metaModel.MClassFormat.sMethods
import de.htwg.zeta.common.format.metaModel.MClassFormat.sName
import de.htwg.zeta.common.format.metaModel.MClassFormat.sOutputs
import de.htwg.zeta.common.format.metaModel.MClassFormat.sSuperTypeNames
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReferenceLinkDef
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OWrites
import play.api.libs.json.Reads

object MClassFormat extends OWrites[MClass] {

  val sName = "name"
  val sDescription = "description"
  val sAbstractness = "abstractness"
  val sSuperTypeNames = "superTypeNames"
  val sInputs = "inputs"
  val sOutputs = "outputs"
  val sAttributes = "attributes"
  val sMethods = "methods"

  override def writes(clazz: MClass): JsObject = Json.obj(
    MClassFormat.sName -> clazz.name,
    sDescription -> clazz.description,
    sAbstractness -> clazz.abstractness,
    sSuperTypeNames -> clazz.superTypeNames,
    sInputs -> JsArray(clazz.inputs.map(MReferenceLinkDefFormat.writes)),
    sOutputs -> JsArray(clazz.outputs.map(MReferenceLinkDefFormat.writes)),
    sAttributes -> JsArray(clazz.attributes.map(MAttributeFormat.writes)),
    sMethods -> JsArray(clazz.methods.map(MethodFormat.writes))
  )

}

case class MClassFormat(enums: Seq[MEnum]) extends Reads[MClass] {

  override def reads(json: JsValue): JsResult[MClass] = {
    for {
      name <- (json \ sName).validate[String]
      description <- (json \ sDescription).validate[String]
      abstractness <- (json \ sAbstractness).validate[Boolean]
      superTypeNames <- (json \ sSuperTypeNames).validate(Reads.list[String])
      inputs <- (json \ sInputs).validate(Reads.list[MReferenceLinkDef])
      outputs <- (json \ sOutputs).validate(Reads.list[MReferenceLinkDef])
      attributes <- (json \ sAttributes).validate(Reads.list(MAttributeFormat(enums)))
      methods <- (json \ sMethods).validate(Reads.list(MethodFormat(enums)))
    } yield {
      MClass(
        name = name,
        description = description,
        abstractness = abstractness,
        superTypeNames = superTypeNames,
        inputs = inputs,
        outputs = outputs,
        attributes = attributes,
        methods = methods
      )
    }
  }

}