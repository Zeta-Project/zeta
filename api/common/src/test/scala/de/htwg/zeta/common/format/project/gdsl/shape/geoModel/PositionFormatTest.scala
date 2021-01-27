package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Position
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

//noinspection ScalaStyle
class PositionFormatTest extends AnyFreeSpec with Matchers {

  "A PositionFormat should" - {
    "write an object" in {
      val result = PositionFormat().writes(Position(10, 12))
      result.toString() shouldBe
        """{"x":10,"y":12}"""
    }
    "read an object" in {
      val result = PositionFormat().reads(Json.parse(
        """{"x":10,"y":12}"""
      ))
      result shouldBe JsSuccess(Position(10, 12))
    }
    "fail in reading an invalid input" in {
      val result = PositionFormat().reads(Json.parse(
        """{"invalid":{"r":23}}"""
      ))
      result.isSuccess shouldBe false
    }
  }

}
