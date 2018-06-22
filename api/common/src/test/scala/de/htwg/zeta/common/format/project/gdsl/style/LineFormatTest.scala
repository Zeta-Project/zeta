package de.htwg.zeta.common.format.project.gdsl.style

import de.htwg.zeta.common.models.project.gdsl.style.Color
import de.htwg.zeta.common.models.project.gdsl.style.Dashed
import de.htwg.zeta.common.models.project.gdsl.style.Dotted
import de.htwg.zeta.common.models.project.gdsl.style.Font
import de.htwg.zeta.common.models.project.gdsl.style.Line
import de.htwg.zeta.common.models.project.gdsl.style.Solid
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json

//noinspection ScalaStyle
class LineFormatTest extends FreeSpec with Matchers {

  "A LineFormat should" - {
    "write a dotted object" in {
      val result = LineFormat().writes(Line(Color(23, 24, 25, 26), Dotted(), 15))
      result.toString() shouldBe
        """{"color":{"r":23,"g":24,"b":25,"a":26,"rgb":"rgb(23,24,25)","rgba":"rgba(23,24,25,26.0)","hex":"#171819"},"style":"dot","width":15}"""
    }
    "write a solid object" in {
      val result = LineFormat().writes(Line(Color(23, 24, 25, 26), Solid(), 15))
      result.toString() shouldBe
        """{"color":{"r":23,"g":24,"b":25,"a":26,"rgb":"rgb(23,24,25)","rgba":"rgba(23,24,25,26.0)","hex":"#171819"},"style":"solid","width":15}"""
    }
    "write a dashed object" in {
      val result = LineFormat().writes(Line(Color(23, 24, 25, 26), Dashed(), 15))
      result.toString() shouldBe
        """{"color":{"r":23,"g":24,"b":25,"a":26,"rgb":"rgb(23,24,25)","rgba":"rgba(23,24,25,26.0)","hex":"#171819"},"style":"dash","width":15}"""
    }
    "read a dotted object" in {
      val result = LineFormat().reads(Json.parse(
        """{"color":"rgba(23,24,25,26)","style":"dot","width":15}"""
      ))
      result shouldBe JsSuccess(Line(Color(23, 24, 25, 26), Dotted(), 15))
    }
    "read a solid object" in {
      val result = LineFormat().reads(Json.parse(
        """{"color":"rgba(23,24,25,26)","style":"solid","width":15}"""
      ))
      result shouldBe JsSuccess(Line(Color(23, 24, 25, 26), Solid(), 15))
    }
    "read a dashed object" in {
      val result = LineFormat().reads(Json.parse(
        """{"color":"rgba(23,24,25,26)","style":"dash","width":15}"""
      ))
      result shouldBe JsSuccess(Line(Color(23, 24, 25, 26), Dashed(), 15))
    }
    "fail in reading an invalid input" in {
      val result = LineFormat().reads(Json.parse(
        """{"invalid":{"r":23}}"""
      ))
      result.isSuccess shouldBe false
    }
  }

}
