package de.htwg.zeta.common.format.metaModel

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClassLinkDef
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat


object MClassLinkDefFormat extends OFormat[MClassLinkDef] {

  private val sClassName = "className"
  private val sUpperBound = "upperBound"
  private val sLowerBound = "lowerBound"
  private val sDeleteIfLower = "deleteIfLower"

  override def writes(link: MClassLinkDef): JsObject = Json.obj(
    sClassName -> link.className,
    sUpperBound -> link.upperBound,
    sLowerBound -> link.lowerBound,
    sDeleteIfLower -> link.deleteIfLower
  )

  override def reads(json: JsValue): JsResult[MClassLinkDef] = {
    for {
      className <- (json \ sClassName).validate[String]
      upperBound <- (json \ sUpperBound).validate[Int]
      lowerBound <- (json \ sLowerBound).validate[Int]
      deleteIfLower <- (json \ sDeleteIfLower).validate[Boolean]
    } yield {
      MClassLinkDef(
        className = className,
        upperBound = upperBound,
        lowerBound = lowerBound,
        deleteIfLower = deleteIfLower
      )
    }

  }

}
