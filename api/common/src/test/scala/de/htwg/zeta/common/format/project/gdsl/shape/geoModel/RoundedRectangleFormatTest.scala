package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Position
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.RoundedRectangle
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Size
import de.htwg.zeta.common.models.project.gdsl.style.Style
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

//noinspection ScalaStyle
class RoundedRectangleFormatTest extends AnyFreeSpec with Matchers {

  "A RoundedRectangleFormat should" - {
    "write an object" in {
      val result = RoundedRectangleFormat(GeoModelFormat.geoModelFormatProvider)
        .writes(RoundedRectangle(
          curve = Size.default,
          size = Size.default,
          position = Position.default,
          childGeoModels = List(),
          style = Style.defaultStyle))
      result.toString() shouldBe
        """{"type":"roundedRectangle","curve":{"width":1,"height":1},"size":{"width":1,"height":1},"position":{"x":0,"y":0},"childGeoElements":[],"style":{"name":"default","description":"default","background":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"}},"font":{"name":"Arial","bold":false,"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"italic":false,"size":10},"line":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"style":"solid","width":1},"transparency":1}}"""
    }
    "read an object" in {
      val result = RoundedRectangleFormat(GeoModelFormat.geoModelFormatProvider)
        .reads(Json.parse(
          """{"type":"roundedRectangle",
           |"size":{"width":1,"height":1},
           |"curve":{"width":1,"height":1},
           |"position":{"x":0,"y":0},
           |"childGeoElements":[],
           |"style":{
           |  "name":"default",
           |  "description":"default",
           |  "background":{"color":"rgba(0,0,0,1.0)"},
           |  "font":{"name":"Arial","bold":false,"color":"rgba(0,0,0,1.0)","italic":false,"size":10},
           |  "line":{"color":"rgba(0,0,0,1.0)","style":"solid","width":1},
           |  "transparency":1}
           |}""".stripMargin
        ))
      result shouldBe JsSuccess(RoundedRectangle(
        curve = Size.default,
        size = Size.default,
        position = Position.default,
        childGeoModels = List(),
        style = Style.defaultStyle))
    }
    "fail in reading an invalid input" in {
      val result = RoundedRectangleFormat(GeoModelFormat.geoModelFormatProvider)
        .reads(Json.parse(
          """{"invalid":{"r":23}}"""
        ))
      result.isSuccess shouldBe false
    }
  }

}
