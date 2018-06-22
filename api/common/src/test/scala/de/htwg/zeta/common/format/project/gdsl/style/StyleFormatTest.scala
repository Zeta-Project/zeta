package de.htwg.zeta.common.format.project.gdsl.style

import de.htwg.zeta.common.models.project.gdsl.style.Background
import de.htwg.zeta.common.models.project.gdsl.style.Color
import de.htwg.zeta.common.models.project.gdsl.style.Dotted
import de.htwg.zeta.common.models.project.gdsl.style.Font
import de.htwg.zeta.common.models.project.gdsl.style.Line
import de.htwg.zeta.common.models.project.gdsl.style.Style
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json

//noinspection ScalaStyle
class StyleFormatTest extends FreeSpec with Matchers {

  "A StyleFormat should" - {
    "write an object" in {
      val result = StyleFormat().writes(Style(
        "TestStyle",
        "What a nice TestStyle!",
        Background(Color(255, 200, 0, 1)),
        Font("Arial", bold = false, Color(0, 0, 0, 0.5), italic = false, 26),
        Line(Color(0, 0, 0, 0.5), Dotted(), 7),
        0.7
      ))
      result.toString() shouldBe
        """{"name":"TestStyle","description":"What a nice TestStyle!","background":{"color":{"r":255,"g":200,"b":0,"a":1,"rgb":"rgb(255,200,0)","rgba":"rgba(255,200,0,1.0)","hex":"#ffc800"}},"font":{"name":"Arial","bold":false,"color":{"r":0,"g":0,"b":0,"a":0.5,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,0.5)","hex":"#000000"},"italic":false,"size":26},"line":{"color":{"r":0,"g":0,"b":0,"a":0.5,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,0.5)","hex":"#000000"},"style":"dot","width":7},"transparency":0.7}"""
    }
    "read an object" in {
      val result = StyleFormat().reads(Json.parse(
        """{"name":"TestStyle",
         |"description":"What a nice TestStyle!",
         |"background":{"color":"rgba(255,200,0,1.0)"},
         |"font":{"name":"Arial","bold":false,"color":"rgba(0,0,0,0.5)","italic":false,"size":26},
         |"line":{"color":"rgba(0,0,0,0.5)","style":"dot","width":7},
         |"transparency":0.7}""".stripMargin
      ))
      result shouldBe JsSuccess(Style(
        "TestStyle",
        "What a nice TestStyle!",
        Background(Color(255, 200, 0, 1)),
        Font("Arial", bold = false, Color(0, 0, 0, 0.5), italic = false, 26),
        Line(Color(0, 0, 0, 0.5), Dotted(), 7),
        0.7
      ))
    }
    "fail in reading an invalid input" in {
      val result = StyleFormat().reads(Json.parse(
        """{"invalid":{"r":23}}"""
      ))
      result.isSuccess shouldBe false
    }
  }

}
