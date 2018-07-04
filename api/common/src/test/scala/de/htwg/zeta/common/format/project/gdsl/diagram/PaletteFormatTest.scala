package de.htwg.zeta.common.format.project.gdsl.diagram

import de.htwg.zeta.common.models.project.gdsl.diagram.Palette
import de.htwg.zeta.common.models.project.gdsl.shape.Edge
import de.htwg.zeta.common.models.project.gdsl.shape.Node
import de.htwg.zeta.common.models.project.gdsl.shape.Placing
import de.htwg.zeta.common.models.project.gdsl.shape.PlacingPosition
import de.htwg.zeta.common.models.project.gdsl.shape.Resizing
import de.htwg.zeta.common.models.project.gdsl.shape.Size
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.GeoModel
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Point
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Polyline
import de.htwg.zeta.common.models.project.gdsl.style.Background
import de.htwg.zeta.common.models.project.gdsl.style.Color
import de.htwg.zeta.common.models.project.gdsl.style.Dotted
import de.htwg.zeta.common.models.project.gdsl.style.Font
import de.htwg.zeta.common.models.project.gdsl.style.Line
import de.htwg.zeta.common.models.project.gdsl.style.LineStyle
import de.htwg.zeta.common.models.project.gdsl.style.Style
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json

//noinspection ScalaStyle
class PaletteFormatTest extends FreeSpec with Matchers {

  "A PaletteFormat should" - {
    "write an object" in {
      val result = PaletteFormat().writes(Palette("testPalette", List(
        Node(
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
        )
      )))
      result.toString() shouldBe
        """{"name":"testPalette","nodes":["TestNode"]}"""
    }
    "read an object" in {
      val result = PaletteFormat().reads(Json.parse(
        """{"name":"testPalette","nodes":["TestNode"]}"""
      ))
      result shouldBe JsSuccess(Palette("testPalette", List()))
    }
    "fail in reading an invalid input" in {
      val result = PaletteFormat().reads(Json.parse(
        """{"invalid":{"r":23}}"""
      ))
      result.isSuccess shouldBe false
    }
  }

}
