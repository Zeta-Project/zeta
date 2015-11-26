package controller

import org.scalajs.dom

import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js._

object ModeController {

  def getAllModesForModel(modelId: String):  Map[String, scalajs.js.Any] = {
    Map[String, scalajs.js.Any](
      "diagram" -> TestLanguage.langForModel(modelId),
      "shape" -> TestLanguage.langForModel(modelId),
      "style" -> TestLanguage.langForModel(modelId)
    )
  }
}
