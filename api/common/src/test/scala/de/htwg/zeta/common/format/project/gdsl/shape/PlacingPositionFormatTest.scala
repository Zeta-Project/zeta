package de.htwg.zeta.common.format.project.gdsl.shape

import de.htwg.zeta.common.models.project.gdsl.shape.PlacingPosition
import de.htwg.zeta.common.models.project.gdsl.shape.Size
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json

//noinspection ScalaStyle
class PlacingPositionFormatTest extends FreeSpec with Matchers {

  "A PlacingPositionFormat should" - {
    "write an object" in {
      val result = PlacingPositionFormat().writes(PlacingPosition(42))
      result.toString() shouldBe
        """{"offset":42}"""
    }
    "read an object" in {
      val result = PlacingPositionFormat().reads(Json.parse(
        """{"offset":42}"""
      ))
      result shouldBe JsSuccess(PlacingPosition(42))
    }
    "fail in reading an invalid input" in {
      val result = PlacingPositionFormat().reads(Json.parse(
        """{"invalid":{"r":23}}"""
      ))
      result.isSuccess shouldBe false
    }
  }

}
