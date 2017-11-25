package de.htwg.zeta.common.format.metaModel

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReferenceLinkDef
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat

object MReferenceLinkDefFormat extends OFormat[MReferenceLinkDef]{

  private val sReferenceName = "type" // TODO this should be changed to "referenceName" in Frontend
  private val sUpperBound = "upperBound"
  private val sLowerBound = "lowerBound"
  private val sDeleteIfLower = "deleteIfLower"

  override def writes(link: MReferenceLinkDef): JsObject = Json.obj(
    sReferenceName -> link.referenceName,
    sUpperBound -> link.upperBound,
    sLowerBound -> link.lowerBound,
    sDeleteIfLower -> link.deleteIfLower
  )

  override def reads(json: JsValue): JsResult[MReferenceLinkDef] = {
    for {
      referenceName <- (json \ sReferenceName).validate[String]
      upperBound <- (json \ sUpperBound).validate[Int]
      lowerBound <- (json \ sLowerBound).validate[Int]
      deleteIfLower <- (json \ sDeleteIfLower).validate[Boolean]
    } yield {
      MReferenceLinkDef(
        referenceName = referenceName,
        upperBound = upperBound,
        lowerBound = lowerBound,
        deleteIfLower = deleteIfLower
      )
    }
  }

}
