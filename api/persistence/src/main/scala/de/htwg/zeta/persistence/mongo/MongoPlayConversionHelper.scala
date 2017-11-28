package de.htwg.zeta.persistence.mongo

import play.api.libs.json.JsObject
import play.api.libs.json.OWrites
import play.api.libs.json.Reads


private[mongo] object MongoPlayConversionHelper {

  private val sId = "id"
  private val sMongoId = "_id"

  def writePlayJson[E](entity: E)(implicit writes: OWrites[E]): JsObject = {
    replaceKey(writes.writes(entity), sId, sMongoId)
  }

  def readPlayJson[E](obj: JsObject)(implicit reads: Reads[E]): E = {
    reads.reads(replaceKey(obj, sMongoId, sId)).getOrElse(
      throw new IllegalArgumentException("parsing from MongoDB failed")
    )
  }

  private def replaceKey(obj: JsObject, oldKey: String, newKey: String): JsObject = {
    obj.value.get(oldKey) match {
      case Some(id) => JsObject(obj.value - oldKey + (newKey -> id))
      case None => obj
    }
  }

}

