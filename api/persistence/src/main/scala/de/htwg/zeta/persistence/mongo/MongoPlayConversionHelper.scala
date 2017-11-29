package de.htwg.zeta.persistence.mongo

import play.api.libs.json.JsObject
import play.api.libs.json.OWrites
import play.api.libs.json.Reads


private[mongo] object MongoPlayConversionHelper {
  
  def writePlayJson[E](entity: E)(implicit writes: OWrites[E]): JsObject = {
    writes.writes(entity)
  }

  def readPlayJson[E](obj: JsObject)(implicit reads: Reads[E]): E = {
    reads.reads(obj).getOrElse(
      throw new IllegalArgumentException("parsing from MongoDB failed")
    )
  }

}

