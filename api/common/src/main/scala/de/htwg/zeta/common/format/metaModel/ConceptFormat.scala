package de.htwg.zeta.common.format.metaModel

import de.htwg.zeta.common.models.modelDefinitions.concept.Concept
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class ConceptFormat(
    enumFormat: EnumFormat,
    classFormat: ClassFormat,
    referenceFormat: ReferenceFormat,
    attributeFormat: AttributeFormat,
    methodFormat: MethodFormat,
    sEnums: String = "enums",
    sClasses: String = "classes",
    sReferences: String = "references",
    sAttributes: String = "attributes",
    sMethods: String = "methods",
    sUiState: String = "uiState"
) extends OFormat[Concept] {

  override def writes(metaModel: Concept): JsObject = Json.obj(
    sEnums -> Writes.seq(enumFormat).writes(metaModel.enums),
    sClasses -> Writes.seq(classFormat).writes(metaModel.classes),
    sReferences -> Writes.seq(referenceFormat).writes(metaModel.references),
    sAttributes -> Writes.seq(attributeFormat).writes(metaModel.attributes),
    sMethods -> Writes.seq(methodFormat).writes(metaModel.methods),
    sUiState -> metaModel.uiState
  )

  override def reads(json: JsValue): JsResult[Concept] = for {
    enums <- (json \ sEnums).validate(Reads.list(enumFormat))
    classes <- (json \ sClasses).validate(Reads.list(classFormat))
    references <- (json \ sReferences).validate(Reads.list(referenceFormat))
    attributes <- (json \ sAttributes).validate(Reads.list(attributeFormat))
    methods <- (json \ sMethods).validate(Reads.list(methodFormat))
    uiState <- (json \ sUiState).validate[String]
  } yield {
    Concept(
      classes = classes,
      references = references,
      enums = enums,
      attributes = attributes,
      methods = methods,
      uiState = uiState
    )
  }

}
