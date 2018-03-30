package de.htwg.zeta.parser.check

import de.htwg.zeta.parser.check.Check.Id
import org.scalatest.FreeSpec
import org.scalatest.Inside
import org.scalatest.Matchers

//noinspection ScalaStyle
class FindGraphCyclesTest extends FreeSpec with Matchers with Inside {

  private def findGraphCycles(elements: List[Element]): List[Id] = {
    val elementToId: Element => Id = element => element.name
    val getElement: Id => Option[Element] = id => elements.find(_.name == id)
    val getParentIds: Element => List[Id] = element => element.parents.toList
    val findGraphCycles = new FindGraphCycles[Element](elementToId, getElement, getParentIds)
    findGraphCycles(elements)
  }

  "Check for graph cycles will result in" - {

    "no graph cycles" - {

      "when an empty list is passed" in {
        val elements: List[Element] = List()
        val graphCycles = findGraphCycles(elements)
        graphCycles shouldBe empty
      }

      "when no parents are extended" in {
        val elements = List(
          Element("A"),
          Element("B"),
          Element("C")
        )
        val graphCycles = findGraphCycles(elements)
        graphCycles shouldBe empty
      }

      "when an acyclic graph is passed" in {
        val elements = List(
          Element("A", parents = "Parent"),
          Element("B", parents = "Parent")
        )
        val graphCycles = findGraphCycles(elements)
        graphCycles shouldBe empty
      }

      "when a diamond graph is passed" in {
        val elements = List(
          Element("A", parents = "B1", "B2"),
          Element("B1", parents = "C"),
          Element("B2", parents = "C"),
          Element("C")
        )
        val graphCycles = findGraphCycles(elements)
        graphCycles shouldBe empty
      }

    }

    "graph cycles" - {

      "when an element extends itself" in {
        val elements = List(
          Element("A", parents = "A")
        )
        val graphCycles = findGraphCycles(elements)
        graphCycles should have size 1
        graphCycles should contain("A")
      }

      "when an element extends itself with a leading legal parent" in {
        val elements = List(
          Element("A"),
          Element("B", parents = "A", "B")
        )
        val graphCycles = findGraphCycles(elements)
        graphCycles should have size 1
        graphCycles should contain("B")
      }

      "in a trivial cycle graph" in {
        val elements = List(
          Element("A", parents = "B"),
          Element("B", parents = "A")
        )
        val graphCycles = findGraphCycles(elements)
        graphCycles should have size 2
        graphCycles should contain("A")
        graphCycles should contain("B")
      }

      "in a trivial cycle graph with a leading legal parent" in {
        val elements = List(
          Element("A"),
          Element("B", parents = "A", "C"),
          Element("C", parents = "A", "B")
        )
        val graphCycles = findGraphCycles(elements)
        graphCycles should have size 2
        graphCycles should contain("B")
        graphCycles should contain("C")
      }

      "in a triangle cycle graph" in {
        val elements = List(
          Element("A", parents = "B"),
          Element("B", parents = "C"),
          Element("C", parents = "A")
        )
        val graphCycles = findGraphCycles(elements)
        graphCycles should have size 3
        graphCycles should contain("A")
        graphCycles should contain("B")
        graphCycles should contain("C")
      }

      "in a modified diamond graph with cycles" in {
        val elements = List(
          Element("A", parents = "C"),
          Element("B1", parents = "A"),
          Element("B2", parents = "A"),
          Element("C", parents = "B1", "B2")
        )
        val graphCycles = findGraphCycles(elements)
        graphCycles should have size 4
        graphCycles should contain("A")
        graphCycles should contain("B1")
        graphCycles should contain("B2")
        graphCycles should contain("C")
      }

    }

  }

}
