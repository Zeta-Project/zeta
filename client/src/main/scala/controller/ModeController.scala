package controller

import org.scalajs.dom

import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js._

object ModeController {

  def getAllModesForModel(modelId: String):  Map[String, scalajs.js.Any] = {
    dom.console.log(TestLanguage.langForModel(modelId))
    Map[String, scalajs.js.Any](
      "Scala" -> "ace/mode/scala",
      "Python" -> "ace/mode/python",
      "MoDiGen" -> TestLanguage.langForModel(modelId),
      "Shape" -> TestLanguage.langForModel(modelId),
      "Style" -> TestLanguage.langForModel(modelId)
    )
  }
}
