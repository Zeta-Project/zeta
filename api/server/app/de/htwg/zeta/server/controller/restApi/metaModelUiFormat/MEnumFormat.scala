package de.htwg.zeta.server.controller.restApi.metaModelUiFormat

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import play.api.libs.json.JsError
import play.api.libs.json.Reads
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsResult
import play.api.libs.json.Format
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.JsArray

private[metaModelUiFormat] object MEnumFormat extends Format[MEnum] {

  private val enumSymbolError = JsError("Enum symbols must be unique and not empty")

  private def checkSymbols(list: List[String]): JsResult[List[String]] = {
    val size = list.size
    // check if elems in list are unique
    if (size == 0 || list.toSet.size != size) {
      enumSymbolError
    } else {
      JsSuccess(list)
    }
  }

  override def writes(o: MEnum): JsValue = {
    Json.obj(
      "mType" -> "mEnum",
      "name" -> o.name,
      "symbols" -> JsArray(o.valueNames.map(JsString))
    )
  }

  override def reads(json: JsValue): JsResult[MEnum] = {
    for {
      name <- json.\("name").validate(Reads.minLength[String](1))
      symbols <- json.\("symbols").validate(Reads.list(Reads.minLength[String](1))).flatMap(checkSymbols)
    } yield {
      MEnum(name, symbols)
    }
  }
}
