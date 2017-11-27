package de.htwg.zeta.common.format.metaModel

import scala.collection.immutable.Seq

import de.htwg.zeta.common.format.metaModel.MClassFormat.sAbstractness
import de.htwg.zeta.common.format.metaModel.MClassFormat.sAttributes
import de.htwg.zeta.common.format.metaModel.MClassFormat.sDescription
import de.htwg.zeta.common.format.metaModel.MClassFormat.sInputReferenceNames
import de.htwg.zeta.common.format.metaModel.MClassFormat.sMethods
import de.htwg.zeta.common.format.metaModel.MClassFormat.sName
import de.htwg.zeta.common.format.metaModel.MClassFormat.sOutputReferenceNames
import de.htwg.zeta.common.format.metaModel.MClassFormat.sSuperTypeNames
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OWrites
import play.api.libs.json.Reads
import play.api.libs.json.Writes

object MClassFormat extends OWrites[MClass] {

  val sName = "name"
  val sDescription = "description"
  val sAbstractness = "abstractness"
  val sSuperTypeNames = "superTypeNames"
  val sInputReferenceNames = "inputReferenceNames"
  val sOutputReferenceNames = "outputReferenceNames"
  val sAttributes = "attributes"
  val sMethods = "methods"

  override def writes(clazz: MClass): JsObject = Json.obj(
    MClassFormat.sName -> clazz.name,
    sDescription -> clazz.description,
    sAbstractness -> clazz.abstractness,
    sSuperTypeNames -> clazz.superTypeNames,
    sInputReferenceNames -> Writes.seq[String].writes(clazz.inputReferenceNames),
    sOutputReferenceNames -> Writes.seq[String].writes(clazz.outputReferenceNames),
    sAttributes -> Writes.seq(MAttributeFormat).writes(clazz.attributes),
    sMethods -> Writes.seq(MethodFormat).writes(clazz.methods)
  )

}

class MClassFormat(enums: Seq[MEnum]) extends Reads[MClass] {

  override def reads(json: JsValue): JsResult[MClass] = {
    for {
      name <- (json \ sName).validate[String]
      description <- (json \ sDescription).validate[String]
      abstractness <- (json \ sAbstractness).validate[Boolean]
      superTypeNames <- (json \ sSuperTypeNames).validate(Reads.list[String])
      inputReferenceNames <- (json \ sInputReferenceNames).validate(Reads.list[String])
      outputReferenceNames <- (json \ sOutputReferenceNames).validate(Reads.list[String])
      attributes <- (json \ sAttributes).validate(Reads.list(new MAttributeFormat(enums)))
      methods <- (json \ sMethods).validate(Reads.list(new MethodFormat(enums)))
    } yield {
      MClass(
        name = name,
        description = description,
        abstractness = abstractness,
        superTypeNames = superTypeNames,
        inputReferenceNames = inputReferenceNames,
        outputReferenceNames = outputReferenceNames,
        attributes = attributes,
        methods = methods
      )
    }
  }

}