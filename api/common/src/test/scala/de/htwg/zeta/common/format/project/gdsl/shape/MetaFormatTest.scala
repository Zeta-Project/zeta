package de.htwg.zeta.common.format.project.gdsl.shape

import de.htwg.zeta.common.models.project.gdsl.shape.Edge
import de.htwg.zeta.common.models.project.gdsl.style.Style
import org.scalatest.FreeSpec
import org.scalatest.Matchers

//noinspection ScalaStyle
class MetaFormatTest extends FreeSpec with Matchers {

  "A MetaFormat should" - {
    "write meta info for link edge concept element" in {
      val result = MetaFormat().writes(Edge(
        name = "TestEdge",
        conceptElement = "Source.link.NodeAsEdge.points",
        target = "TestNode",
        style = Style.defaultStyle,
        placings = List()
      ))
      result.toString() shouldBe
        """{"source":{"mclass":"Source","mref":"link"},"target":{"mclass":"TestNode","mref":"points"},"forMClass":"NodeAsEdge"}"""
    }
    "write meta info for simple named concept element" in {
      val result = MetaFormat().writes(Edge(
        name = "TestEdge",
        conceptElement = "LinkTest",
        target = "TestNode",
        style = Style.defaultStyle,
        placings = List()
      ))
      result.toString() shouldBe "null"
    }
  }

}
