package de.htwg.zeta.common.format.metaModel

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClassLinkDef
import play.api.libs.json.Format
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsNumber
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsString
import play.api.libs.json.JsValue


// replacement for MLinkDef
object MClassLinkDefFormat {
  private val sClassName = "type" // TODO this should be changed to "className" in Frontend
  private val sUpperBound = "upperBound"
  private val sLowerBound = "lowerBound"
  private val sDeleteIfLower = "deleteIfLower"

  implicit val playJsonFormat: Format[MClassLinkDef] = new Format[MClassLinkDef] {

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

    override def writes(link: MClassLinkDef): JsValue = {
      JsObject(Map(
        sClassName -> JsString(link.className),
        sUpperBound -> JsNumber(link.upperBound),
        sLowerBound -> JsNumber(link.lowerBound),
        sDeleteIfLower -> JsBoolean(link.deleteIfLower)
      ))
    }

  }
}
