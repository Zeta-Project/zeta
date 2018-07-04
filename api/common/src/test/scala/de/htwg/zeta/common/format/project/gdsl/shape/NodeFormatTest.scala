package de.htwg.zeta.common.format.project.gdsl.shape

import de.htwg.zeta.common.models.project.gdsl.shape.Edge
import de.htwg.zeta.common.models.project.gdsl.shape.Node
import de.htwg.zeta.common.models.project.gdsl.shape.Resizing
import de.htwg.zeta.common.models.project.gdsl.shape.Size
import de.htwg.zeta.common.models.project.gdsl.style.Style
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json

//noinspection ScalaStyle
class NodeFormatTest extends FreeSpec with Matchers {

  "A NodeFormat should" - {
    "write an object" in {
      val result = NodeFormat().writes(Node(
        name = "TestNode",
        conceptElement = "TextNodeConcept",
        edges = List(Edge(
          name = "TestEdge",
          conceptElement = "LinkTest",
          target = "TestNode",
          style = Style.defaultStyle,
          placings = List()
        )),
        size = Size(10, 15, 15, 5, 20, 10),
        style = Style.defaultStyle,
        resizing = Resizing(horizontal = true, vertical = true, proportional = false),
        geoModels = List()
      ))
      result.toString() shouldBe
        """{"name":"TestNode","conceptElement":"TextNodeConcept","edges":[{"name":"TestEdge","conceptElement":"LinkTest","target":"TestNode","style":{"name":"default","description":"default","background":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"}},"font":{"name":"Arial","bold":false,"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"italic":false,"size":10},"line":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"style":"solid","width":1},"transparency":1},"placings":[],"meta":null}],"size":{"width":10,"height":15,"widthMax":15,"widthMin":5,"heightMax":20,"heightMin":10},"style":{"name":"default","description":"default","background":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"}},"font":{"name":"Arial","bold":false,"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"italic":false,"size":10},"line":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"style":"solid","width":1},"transparency":1},"resizing":{"horizontal":true,"vertical":true,"proportional":false},"geoElements":[]}"""
    }
    "read an object" in {
      val result = NodeFormat().reads(Json.parse(
        """
         |{"name":"TestNode",
         |  "conceptElement":"TextNodeConcept",
         |  "edges":[
         |    {"name":"TestEdge",
         |    "conceptElement":"LinkTest",
         |    "target":"TestNode",
         |    "style":{"name":"default",
         |      "description":"default",
         |      "background":{"color":"rgba(0,0,0,1.0)"},
         |      "font":{"name":"Arial","bold":false,"color":"rgba(0,0,0,1.0)","italic":false,"size":10},
         |      "line":{"color":"rgba(0,0,0,1.0)","style":"solid","width":1},
         |      "transparency":1},
         |    "placings":[],
         |    "meta":null}
         |  ],
         |  "size":{"width":10,"height":15,"widthMax":15,"widthMin":5,"heightMax":20,"heightMin":10},
         |  "style":{"name":"default",
         |    "description":"default",
         |    "background":{"color":"rgba(0,0,0,1.0)"},
         |    "font":{"name":"Arial","bold":false,"color":"rgba(0,0,0,1.0)","italic":false,"size":10},
         |    "line":{"color":"rgba(0,0,0,1.0)","style":"solid","width":1},
         |    "transparency":1},
         |  "resizing":{"horizontal":true,"vertical":true,"proportional":false},
         |  "geoElements":[]
         |}""".stripMargin
      ))
      result shouldBe JsSuccess(Node(
        name = "TestNode",
        conceptElement = "TextNodeConcept",
        edges = List(Edge(
          name = "TestEdge",
          conceptElement = "LinkTest",
          target = "TestNode",
          style = Style.defaultStyle,
          placings = List()
        )),
        size = Size(10, 15, 15, 5, 20, 10),
        style = Style.defaultStyle,
        resizing = Resizing(horizontal = true, vertical = true, proportional = false),
        geoModels = List()
      ))
    }
    "fail in reading an invalid input" in {
      val result = NodeFormat().reads(Json.parse(
        """{"invalid":{"r":23}}"""
      ))
      result.isSuccess shouldBe false
    }
  }

}
