package de.htwg.zeta.common.format.project.gdsl.shape

import de.htwg.zeta.common.models.project.gdsl.shape.Placing
import de.htwg.zeta.common.models.project.gdsl.shape.PlacingPosition
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Ellipse
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Position
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Size
import de.htwg.zeta.common.models.project.gdsl.style.Style
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json

//noinspection ScalaStyle
class PlacingFormatTest extends AnyFreeSpec with Matchers {

  "A PlacingFormat should" - {
    "write an object" in {
      val result = PlacingFormat()
        .writes(Placing(
          position = PlacingPosition(1.5),
          geoModel = Ellipse(
            size = Size.default,
            position = Position.default,
            childGeoModels = List(),
            style = Style.defaultStyle),
          style = Style.defaultStyle))
      result.toString() shouldBe
        """{"style":{"name":"default","description":"default","background":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"}},"font":{"name":"Arial","bold":false,"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"italic":false,"size":10},"line":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"style":"solid","width":1},"transparency":1},"position":{"offset":1.5},"geoElement":{"childGeoElements":[],"size":{"width":1,"height":1},"style":{"name":"default","description":"default","background":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"}},"font":{"name":"Arial","bold":false,"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"italic":false,"size":10},"line":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"style":"solid","width":1},"transparency":1},"position":{"x":0,"y":0},"id":"00000000-0000-0000-0000-0000e9be276f","type":"ellipse"}}"""
    }
    "read an object" in {
      val result = PlacingFormat()
        .reads(Json.parse(
          """{"position":{"offset":1.5},
           |"geoElement":{"type":"ellipse",
           | "size":{"width":1,"height":1},
           | "position":{"x":0,"y":0},
           | "childGeoElements":[],
           | "style":{
           |   "name":"default",
           |   "description":"default",
           |   "background":{"color":"rgba(0,0,0,1.0)"},
           |   "font":{"name":"Arial","bold":false,"color":"rgba(0,0,0,1.0)","italic":false,"size":10},
           |   "line":{"color":"rgba(0,0,0,1.0)","style":"solid","width":1},
           |   "transparency":1}
           | },
           |"style":{
           |  "name":"default",
           |  "description":"default",
           |  "background":{"color":"rgba(0,0,0,1.0)"},
           |  "font":{"name":"Arial","bold":false,"color":"rgba(0,0,0,1.0)","italic":false,"size":10},
           |  "line":{"color":"rgba(0,0,0,1.0)","style":"solid","width":1},
           |  "transparency":1}
           |}""".stripMargin
        ))
      result shouldBe JsSuccess(Placing(
        position = PlacingPosition(1.5),
        geoModel = Ellipse(
          size = Size.default,
          position = Position.default,
          childGeoModels = List(),
          style = Style.defaultStyle),
        style = Style.defaultStyle))
    }
    "fail in reading an invalid input" in {
      val result = PlacingFormat()
        .reads(Json.parse(
          """{"invalid":{"r":23}}"""
        ))
      result.isSuccess shouldBe false
    }
  }

}
