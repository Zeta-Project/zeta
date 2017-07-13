package de.htwg.zeta.common.models.frontend

import grizzled.slf4j.Logging
import play.api.libs.json.Json
import play.api.libs.json.JsValue


object SafeFormats extends Logging {

  def safeWrite(value: => JsValue): JsValue = {
    try {
      value
    } catch {
      case e: Exception =>
        error("failed writing to json: " + e.toString)
        Json.obj(
          "type" -> "Error",
          "message" -> e.getMessage
        )
    }
  }

  def safeRead[T](value: => T): T = {
    try {
      value
    } catch {
      case e: Exception =>
        error("failed reading from json: " + e.toString)
        value
    }
  }

}
