package de.htwg.zeta.common.format.metaModel

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat
import play.api.libs.json.Reads


object MetaModelFormat extends OFormat[MetaModel] {

  private val sName = "name"
  private val sEnums = "enums"
  private val sClasses = "classes"
  private val sReferences = "references"
  private val sAttributes = "attributes"
  private val sMethods = "methods"
  private val sUiState = "uiState"

  override def writes(metaModel: MetaModel): JsObject = Json.obj(
    sName -> metaModel.name,
    sEnums -> JsArray(metaModel.enums.map(MEnumFormat.writes)),
    sClasses -> JsArray(metaModel.classes.map(MClassFormat.writes)),
    sReferences -> JsArray(metaModel.references.map(MReferenceFormat.writes)),
    sAttributes -> JsArray(metaModel.attributes.map(MAttributeFormat.writes)),
    sMethods -> JsArray(metaModel.methods.map(MethodFormat.writes)),
    sUiState -> metaModel.uiState
  )

  override def reads(json: JsValue): JsResult[MetaModel] = {
    for {
      name <- (json \ sName).validate[String]
      enums <- (json \ sEnums).validate(Reads.list(MEnumFormat))
      classes <- (json \ sClasses).validate(Reads.list(MClassFormat(enums)))
      references <- (json \ sReferences).validate(Reads.list(MReferenceFormat(enums)))
      attributes <- (json \ sAttributes).validate(Reads.list(MAttributeFormat(enums)))
      methods <- (json \ sMethods).validate(Reads.list(MethodFormat(enums)))
      uiState <- (json \ sUiState).validate[String]
    } yield {
      MetaModel(
        name = name,
        classes = classes,
        references = references,
        enums = enums,
        attributes = attributes,
        methods = methods,
        uiState = uiState
      )
    }
  }

  val empty: Reads[MetaModel] = Reads { json =>
    (json \ sName).validate[String].map(MetaModel.empty)
  }

}
