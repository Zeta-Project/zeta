package de.htwg.zeta.persistence.mongo

import play.api.libs.json.JsError
import play.api.libs.json.JsObject
import play.api.libs.json.JsSuccess
import play.api.libs.json.OWrites
import play.api.libs.json.Reads


private[mongo] object MongoPlayConversionHelper {

  def writePlayJson[E](entity: E)(implicit writes: OWrites[E]): JsObject = {
    writes.writes(entity)
  }

  def readPlayJson[E](obj: JsObject)(implicit reads: Reads[E]): E = {
    obj.validate(reads) match {
      case JsSuccess(value, _) => value
      case JsError(e) => throw new IllegalArgumentException(s"parsing from MongoDB failed: ${e.mkString}")
    }
  }

}
