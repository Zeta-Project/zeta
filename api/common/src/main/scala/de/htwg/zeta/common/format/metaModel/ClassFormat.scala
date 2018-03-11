package de.htwg.zeta.common.format.metaModel

import de.htwg.zeta.common.models.project.concept.elements.MClass
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class ClassFormat(
    attributeFormat: AttributeFormat,
    methodFormat: MethodFormat,
    sName: String = "name",
    sDescription: String = "description",
    sAbstractness: String = "abstractness",
    sSuperTypeNames: String = "superTypeNames",
    sInputReferenceNames: String = "inputReferenceNames",
    sOutputReferenceNames: String = "outputReferenceNames",
    sAttributes: String = "attributes",
    sMethods: String = "methods"
) extends OFormat[MClass] {

  override def writes(clazz: MClass): JsObject = Json.obj(
    sName -> clazz.name,
    sDescription -> clazz.description,
    sAbstractness -> clazz.abstractness,
    sSuperTypeNames -> clazz.superTypeNames,
    sInputReferenceNames -> Writes.seq[String].writes(clazz.inputReferenceNames),
    sOutputReferenceNames -> Writes.seq[String].writes(clazz.outputReferenceNames),
    sAttributes -> Writes.seq(attributeFormat).writes(clazz.attributes),
    sMethods -> Writes.seq(methodFormat).writes(clazz.methods)
  )

  override def reads(json: JsValue): JsResult[MClass] = for {
    name <- (json \ sName).validate[String]
    description <- (json \ sDescription).validate[String]
    abstractness <- (json \ sAbstractness).validate[Boolean]
    superTypeNames <- (json \ sSuperTypeNames).validate(Reads.list[String])
    inputReferenceNames <- (json \ sInputReferenceNames).validate(Reads.list[String])
    outputReferenceNames <- (json \ sOutputReferenceNames).validate(Reads.list[String])
    attributes <- (json \ sAttributes).validate(Reads.list(attributeFormat))
    methods <- (json \ sMethods).validate(Reads.list(methodFormat))
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
