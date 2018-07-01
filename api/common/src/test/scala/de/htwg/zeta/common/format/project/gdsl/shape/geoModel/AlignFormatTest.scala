package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Align
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json

//noinspection ScalaStyle
class AlignFormatTest extends FreeSpec with Matchers {

  "A AlignFormat should" - {
    "write a top left object" in {
      val result = AlignFormat().writes(Align(
        horizontal = Align.Horizontal.left,
        vertical = Align.Vertical.top))
      result.toString() shouldBe
        """{"horizontal":"left","vertical":"top"}"""
    }
    "read a top left object" in {
      val result = AlignFormat()
        .reads(Json.parse(
          """{"horizontal": "left", "vertical":"top"}""".stripMargin
        ))
      result shouldBe JsSuccess(Align(
        horizontal = Align.Horizontal.left,
        vertical = Align.Vertical.top))
    }
    "write a middle middle object" in {
      val result = AlignFormat().writes(Align(
        horizontal = Align.Horizontal.middle,
        vertical = Align.Vertical.middle))
      result.toString() shouldBe
        """{"horizontal":"middle","vertical":"middle"}"""
    }
    "read a middle middle object" in {
      val result = AlignFormat()
        .reads(Json.parse(
          """{"horizontal": "middle", "vertical":"middle"}""".stripMargin
        ))
      result shouldBe JsSuccess(Align(
        horizontal = Align.Horizontal.middle,
        vertical = Align.Vertical.middle))
    }
    "write a bottom right object" in {
      val result = AlignFormat().writes(Align(
        horizontal = Align.Horizontal.right,
        vertical = Align.Vertical.bottom))
      result.toString() shouldBe
        """{"horizontal":"right","vertical":"bottom"}"""
    }
    "read a bottom right object" in {
      val result = AlignFormat()
        .reads(Json.parse(
          """{"horizontal": "right", "vertical":"bottom"}""".stripMargin
        ))
      result shouldBe JsSuccess(Align(
        horizontal = Align.Horizontal.right,
        vertical = Align.Vertical.bottom))
    }
    "fail in reading an invalid input" in {
      val result = AlignFormat()
        .reads(Json.parse(
          """{"invalid":{"r":23}}"""
        ))
      result.isSuccess shouldBe false
    }
  }

}
