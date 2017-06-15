package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import play.api.libs.json.Format
import play.api.libs.json.Json

/** MLinkDef implementation */
case class MClassLinkDef(
    className: String,
    upperBound: Int,
    lowerBound: Int,
    deleteIfLower: Boolean
) extends MBounds

object MClassLinkDef {

  implicit val playJsonFormat: Format[MClassLinkDef] = Json.format[MClassLinkDef]

}
