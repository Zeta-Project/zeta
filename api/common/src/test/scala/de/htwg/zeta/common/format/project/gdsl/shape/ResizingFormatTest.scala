package de.htwg.zeta.common.format.project.gdsl.shape

import de.htwg.zeta.common.models.project.gdsl.shape.Resizing
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json

//noinspection ScalaStyle
class ResizingFormatTest extends FreeSpec with Matchers {

  "A ResizingFormat should" - {
    "write an object" in {
      val result = ResizingFormat().writes(Resizing(horizontal = true, vertical = true, proportional = false))
      result.toString() shouldBe
        """{"horizontal":true,"vertical":true,"proportional":false}"""
    }
    "read an object" in {
      val result = ResizingFormat().reads(Json.parse(
        """{"horizontal":true,"vertical":true,"proportional":false}"""
      ))
      result shouldBe JsSuccess(Resizing(horizontal = true, vertical = true, proportional = false))
    }
    "fail in reading an invalid input" in {
      val result = ResizingFormat().reads(Json.parse(
        """{"invalid":{"r":23}}"""
      ))
      result.isSuccess shouldBe false
    }
  }

}
