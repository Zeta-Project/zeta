package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.VerticalLayout
import de.htwg.zeta.common.models.project.gdsl.style.Style
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

//noinspection ScalaStyle
class VerticalLayoutFormatTest extends AnyFreeSpec with Matchers {

  "A VerticalLayoutFormat should" - {
    "write an object" in {
      val result = VerticalLayoutFormat(GeoModelFormat.geoModelFormatProvider)
        .writes(VerticalLayout(List(VerticalLayout(List(), Style.defaultStyle)), Style.defaultStyle))
      result.toString() shouldBe
        """{"type":"verticalLayout","childGeoElements":[{"childGeoElements":[],"style":{"name":"default","description":"default","background":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"}},"font":{"name":"Arial","bold":false,"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"italic":false,"size":10},"line":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"style":"solid","width":1},"transparency":1},"id":"00000000-0000-0000-0000-0000ffe0a5e5","type":"verticalLayout"}],"style":{"name":"default","description":"default","background":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"}},"font":{"name":"Arial","bold":false,"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"italic":false,"size":10},"line":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"style":"solid","width":1},"transparency":1}}"""
    }
    "read an object" in {
      val result = VerticalLayoutFormat(GeoModelFormat.geoModelFormatProvider)
        .reads(Json.parse(
          """{"type":"verticalLayout",
           |"childGeoElements":[
           |  {"type":"verticalLayout",
           |  "childGeoElements":[],
           |  "style":{
           |    "name":"default",
           |    "description":"default",
           |    "background":{"color":"rgba(0,0,0,1.0)"},
           |    "font":{"name":"Arial","bold":false,"color":"rgba(0,0,0,1.0)","italic":false,"size":10},
           |    "line":{"color":"rgba(0,0,0,1.0)","style":"solid","width":1},
           |    "transparency":1}
           |  }
           |],
           |"style":{
           |  "name":"default",
           |  "description":"default",
           |  "background":{"color":"rgba(0,0,0,1.0)"},
           |  "font":{"name":"Arial","bold":false,"color":"rgba(0,0,0,1.0)","italic":false,"size":10},
           |  "line":{"color":"rgba(0,0,0,1.0)","style":"solid","width":1},
           |  "transparency":1}
           |}""".stripMargin
        ))
      result shouldBe JsSuccess(VerticalLayout(List(VerticalLayout(List(), Style.defaultStyle)), Style.defaultStyle))
    }
    "fail in reading an invalid input" in {
      val result = VerticalLayoutFormat(GeoModelFormat.geoModelFormatProvider)
        .reads(Json.parse(
          """{"invalid":{"r":23}}"""
        ))
      result.isSuccess shouldBe false
    }
  }

}
