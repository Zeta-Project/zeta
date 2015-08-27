package controller

import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js._

object ModeController {

  def getAllModesForModel(modelId: String):  Map[String, scalajs.js.Any] = Map[String, scalajs.js.Any](
    "Scala" -> "Scala",
    "Python" -> "Python",
    "TestMode" -> TestLanguage.langForModel(modelId)
  )
}
