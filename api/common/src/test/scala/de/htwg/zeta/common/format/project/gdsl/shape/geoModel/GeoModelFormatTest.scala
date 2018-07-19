package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.VerticalLayout
import de.htwg.zeta.common.models.project.gdsl.style.Style
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json

//noinspection ScalaStyle
class GeoModelFormatTest extends FreeSpec with Matchers {

  "A GeoModelFormat should" - {
    "write an object and add id" in {
      val result = GeoModelFormat()
        .writes(VerticalLayout(List(VerticalLayout(List(), Style.defaultStyle)), Style.defaultStyle))
      result.toString() shouldBe
        """{"type":"verticalLayout","childGeoElements":[{"type":"verticalLayout","childGeoElements":[],"style":{"name":"default","description":"default","background":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"}},"font":{"name":"Arial","bold":false,"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"italic":false,"size":10},"line":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"style":"solid","width":1},"transparency":1},"id":"00000000-0000-0000-0000-0000ffe0a5e5"}],"style":{"name":"default","description":"default","background":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"}},"font":{"name":"Arial","bold":false,"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"italic":false,"size":10},"line":{"color":{"r":0,"g":0,"b":0,"a":1,"rgb":"rgb(0,0,0)","rgba":"rgba(0,0,0,1.0)","hex":"#000000"},"style":"solid","width":1},"transparency":1},"id":"00000000-0000-0000-0000-0000d094577b"}"""
    }
    "read an object" in {
      val result = GeoModelFormat()
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
  }

}
