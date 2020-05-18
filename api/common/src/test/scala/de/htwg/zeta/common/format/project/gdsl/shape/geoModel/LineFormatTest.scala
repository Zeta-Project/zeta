package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Line
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Point
import de.htwg.zeta.common.models.project.gdsl.style.Style
import org.scalatest.matchers.should.Matchers
import org.scalatest.freespec.AnyFreeSpec
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json

//noinspection ScalaStyle
class LineFormatTest extends AnyFreeSpec with Matchers {

  "A LineFormat should" - {
    "write an object" in {
      val result = LineFormat(GeoModelFormat.geoModelFormatProvider)
        .writes(Line(
          startPoint = Point(12, 23),
          endPoint = Point(11, 24),
          childGeoModels = List(),
          style = Style.defaultStyle))
      result.toString() shouldBe
        """{"type":"line","startPoint":{"x":12,"y":23},"endPoint":{"x":11,"y":24},"childGeoElements":[],"style":{"name":"default","description":"default","background":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"}},"font":{"name":"Arial","bold":false,"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"italic":false,"size":10},"line":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"style":"solid","width":1},"transparency":1}}"""
    }
    "read an object" in {
      val result = LineFormat(GeoModelFormat.geoModelFormatProvider)
        .reads(Json.parse(
          """{"type":"line",
           |"startPoint":{"x":12,"y":23},
           |"endPoint":{"x":11,"y":24},
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
      result shouldBe JsSuccess(Line(
        startPoint = Point(12, 23),
        endPoint = Point(11, 24),
        childGeoModels = List(),
        style = Style.defaultStyle))
    }
    "fail in reading an invalid input" in {
      val result = LineFormat(GeoModelFormat.geoModelFormatProvider)
        .reads(Json.parse(
          """{"invalid":{"r":23}}"""
        ))
      result.isSuccess shouldBe false
    }
  }

}
