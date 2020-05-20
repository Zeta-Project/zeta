package de.htwg.zeta.common.format.project.gdsl.shape

import de.htwg.zeta.common.models.project.gdsl.shape.Edge
import de.htwg.zeta.common.models.project.gdsl.style.Style
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

//noinspection ScalaStyle
class MetaFormatTest extends AnyFreeSpec with Matchers {

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
