package de.htwg.zeta.server.controller.restApi.metaModelUIJsonFormat

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MBounds
import play.api.data.validation.ValidationError
import play.api.libs.json.JsValue
import play.api.libs.json.JsError
import play.api.libs.json.JsResult
import play.api.libs.json.Format

private[metaModelUIJsonFormat] trait MBoundsFormat[MB <: MBounds] extends Format[MB] { // scalastyle:ignore
  private val boundsError = JsError(ValidationError("invalid lower and/or upper bound"))

  private def boundsCheck(bounds: MBounds): Boolean = {
    (bounds.upperBound > bounds.lowerBound) ||
      (bounds.upperBound == bounds.lowerBound && bounds.lowerBound != 0) ||
      (bounds.upperBound == -1)
  }

  override final def reads(json: JsValue): JsResult[MB] =
    readsUnchecked(json).filter(boundsError)(boundsCheck)

  def readsUnchecked(json: JsValue): JsResult[MB]
}
