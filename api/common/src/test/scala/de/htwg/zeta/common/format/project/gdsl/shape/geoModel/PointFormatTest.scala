package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Point
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json

//noinspection ScalaStyle
class PointFormatTest extends FreeSpec with Matchers {

  "A PointFormat should" - {
    "write an object" in {
      val result = PointFormat().writes(Point(10, 12))
      result.toString() shouldBe
        """{"x":10,"y":12}"""
    }
    "read an object" in {
      val result = PointFormat().reads(Json.parse(
        """{"x":10,"y":12}"""
      ))
      result shouldBe JsSuccess(Point(10, 12))
    }
    "fail in reading an invalid input" in {
      val result = PointFormat().reads(Json.parse(
        """{"invalid":{"r":23}}"""
      ))
      result.isSuccess shouldBe false
    }
  }

}
