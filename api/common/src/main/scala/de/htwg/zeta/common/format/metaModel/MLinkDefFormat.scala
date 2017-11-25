package de.htwg.zeta.common.format.metaModel

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MBounds
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Reads

trait MLinkDefFormat[MB <: MBounds] extends MBoundsFormat[MB] { // scalastyle:ignore
  override final def readsUnchecked(jsv: JsValue): JsResult[MB] = {
    for {
      refName <- jsv.\("type").validate[String]
      upperBound <- jsv.\("upperBound").validate[Int](Reads.min(-1))
      lowerBound <- jsv.\("lowerBound").validate[Int](Reads.min(0))
      deleteIfLower <- jsv.\("deleteIfLower").validate[Boolean]
    } yield {
      buildMLink(refName, upperBound, lowerBound, deleteIfLower)
    }
  }

  def buildMLink(name: String, upperBound: Int, lowerBound: Int, deleteIfLower: Boolean): MB

  override final def writes(mLink: MB): JsValue = {
    val (name: String, upperBound: Int, lowerBound: Int, deleteIfLower: Boolean) = destroyMLink(mLink)
    Json.obj(
      "type" -> name,
      "upperBound" -> upperBound,
      "lowerBound" -> lowerBound,
      "deleteIfLower" -> deleteIfLower
    )
  }

  def destroyMLink(mb: MB): (String, Int, Int, Boolean)
}
