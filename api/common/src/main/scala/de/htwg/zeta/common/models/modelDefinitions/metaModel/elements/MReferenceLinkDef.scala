package models.modelDefinitions.metaModel.elements

import play.api.libs.json.Format
import play.api.libs.json.Json

/** MLinkDef implementation */
case class MReferenceLinkDef(
    referenceName: String,
    upperBound: Int,
    lowerBound: Int,
    deleteIfLower: Boolean)
  extends MBounds

object MReferenceLinkDef {

  implicit val playJsonFormat: Format[MReferenceLinkDef] = Json.format[MReferenceLinkDef]

}
