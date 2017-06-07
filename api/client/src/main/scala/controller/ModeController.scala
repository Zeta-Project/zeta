package controller

import java.util.UUID

object ModeController {

  def getAllModesForModel(modelId: UUID): Map[String, scalajs.js.Any] = {
    Map[String, scalajs.js.Any](
      "diagram" -> TestLanguage.langForModel(modelId),
      "shape" -> TestLanguage.langForModel(modelId),
      "style" -> StyleLanguage.langForModel(modelId)
    )
  }
}
