package de.htwg.zeta.common.format.project.gdsl.style

import de.htwg.zeta.common.models.project.gdsl.style.Color
import de.htwg.zeta.common.models.project.gdsl.style.Font
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json

//noinspection ScalaStyle
class FontFormatTest extends FreeSpec with Matchers {

  "A ColorFormatTest should" - {
    "write an object" in {
      val result = FontFormat().writes(Font("Arial", bold = true, Color(23, 24, 25, 26), italic = true, 16))
      result.toString() shouldBe
        """{"name":"Arial","bold":true,"color":{"r":23,"g":24,"b":25,"a":26,"rgb":"rgb(23,24,25)","rgba":"rgba(23,24,25,26.0)","hex":"#171819"},"italic":true,"size":16}"""
    }
    "read an object" in {
      val result = FontFormat().reads(Json.parse(
        """{"name":"Arial","bold":true,"color":"rgba(23,24,25,26)","italic":true,"size":16}"""
      ))
      result shouldBe JsSuccess(Font("Arial", bold = true, Color(23, 24, 25, 26), italic = true, 16))
    }
    "fail in reading an invalid input" in {
      val result = FontFormat().reads(Json.parse(
        """{"invalid":{"r":23}}"""
      ))
      result.isSuccess shouldBe false
    }
  }

}
