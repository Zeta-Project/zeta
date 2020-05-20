package de.htwg.zeta.common.format.project.gdsl.shape

import de.htwg.zeta.common.models.project.gdsl.shape.Edge
import de.htwg.zeta.common.models.project.gdsl.style.Style
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json

//noinspection ScalaStyle
class EdgeFormatTest extends AnyFreeSpec with Matchers {

  "A EdgeFormat should" - {
    "write an object" in {
      val result = EdgeFormat().writes(Edge(
        name = "TestEdge",
        conceptElement = "LinkTest",
        target = "TestNode",
        style = Style.defaultStyle,
        placings = List()
      ))
      result.toString() shouldBe
        """{"name":"TestEdge","conceptElement":"LinkTest","target":"TestNode","style":{"name":"default","description":"default","background":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"}},"font":{"name":"Arial","bold":false,"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"italic":false,"size":10},"line":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"style":"solid","width":1},"transparency":1},"placings":[],"meta":null}"""
    }
    "read an object" in {
      val result = EdgeFormat().reads(Json.parse(
        """
         |{"name":"TestEdge",
         |  "conceptElement":"LinkTest",
         |  "target":"TestNode",
         |  "style":{"name":"default",
         |    "description":"default",
         |    "background":{"color":"rgba(0,0,0,1.0)"},
         |    "font":{"name":"Arial","bold":false,"color":"rgba(0,0,0,1.0)","italic":false,"size":10},
         |    "line":{"color":"rgba(0,0,0,1.0)","style":"solid","width":1},
         |    "transparency":1},
         |  "placings":[],
         |  "meta":null
         |}""".stripMargin
      ))
      result shouldBe JsSuccess(Edge(
        name = "TestEdge",
        conceptElement = "LinkTest",
        target = "TestNode",
        style = Style.defaultStyle,
        placings = List()
      ))
    }
    "fail in reading an invalid input" in {
      val result = EdgeFormat().reads(Json.parse(
        """{"invalid":{"r":23}}"""
      ))
      result.isSuccess shouldBe false
    }
  }

}
