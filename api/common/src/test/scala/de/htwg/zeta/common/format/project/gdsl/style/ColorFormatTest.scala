package de.htwg.zeta.common.format.project.gdsl.style

import de.htwg.zeta.common.models.project.gdsl.style.Color
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

//noinspection ScalaStyle
class ColorFormatTest extends AnyFreeSpec with Matchers {

  "A ColorFormat should" - {
    "write an object" in {
      val result = ColorFormat().writes(Color(23, 24, 25, 26))
      result.toString() shouldBe
        """{"r":23,"g":24,"b":25,"a":26,"rgb":"rgb(23,24,25)","rgba":"rgba(23,24,25,26.0)","hex":"#171819"}"""
    }
    "read an object" in {
      val result = ColorFormat().reads(JsString("rgba(23,24,25,26)"))
      result shouldBe JsSuccess(Color(23, 24, 25, 26))
    }
    "fail in reading an invalid input" in {
      val result = ColorFormat().reads(JsString("invalid"))
      result.isSuccess shouldBe false
    }
  }

}
