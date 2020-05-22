package de.htwg.zeta.common.format.project.gdsl.style

import de.htwg.zeta.common.models.project.gdsl.style.Background
import de.htwg.zeta.common.models.project.gdsl.style.Color
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

//noinspection ScalaStyle
class BackgroundFormatTest extends AnyFreeSpec with Matchers {

  "A BackgroundFormat should" - {
    "write an object" in {
      val result = BackgroundFormat().writes(Background(Color(23, 24, 25, 26)))
      result.toString() shouldBe
        """{"color":{"r":23,"g":24,"b":25,"a":26,"rgb":"rgb(23,24,25)","rgba":"rgba(23,24,25,26.0)","hex":"#171819"}}"""
    }
    "read an object" in {
      val result = BackgroundFormat().reads(Json.parse(
        """{"color":"rgba(23,24,25,26)"}"""
      ))
      result shouldBe JsSuccess(Background(Color(23, 24, 25, 26)))
    }
    "fail in reading an invalid input" in {
      val result = BackgroundFormat().reads(Json.parse(
        """{"invalid":{"r":23}}"""
      ))
      result.isSuccess shouldBe false
    }
  }

}
