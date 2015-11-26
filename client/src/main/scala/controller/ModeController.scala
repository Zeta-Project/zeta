package controller

object ModeController {

  def getAllModesForModel(modelId: String):  Map[String, scalajs.js.Any] = {
    Map[String, scalajs.js.Any](
      "diagram" -> TestLanguage.langForModel(modelId),
      "shape" -> TestLanguage.langForModel(modelId),
      "style" -> TestLanguage.langForModel(modelId)
    )
  }
}
