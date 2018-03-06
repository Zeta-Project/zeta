package de.htwg.zeta.parser

import org.scalatest.FreeSpec
import org.scalatest.Matchers

//noinspection ScalaStyle
class ReferenceCollectorTest extends FreeSpec with Matchers {

  private case class Element(name: String, index: Int)
  private val list = List(Element("1", 0), Element("2", 1), Element("2", 2), Element("3", 3), Element("4", 4))
  private val collector = ReferenceCollector[Element](list, _.name)

  "A reference collector" - {

    "can collect" - {

      "all list elements by their id with *" in {
        val result = collector.*("1")
        result.size shouldBe 1
        result.head shouldBe Element("1", 0)
      }

      "all list elements (duplicates) by their id with *" in {
        val result = collector.*("2")
        result.size shouldBe 2
        result.head shouldBe Element("2", 1)
        result(1) shouldBe Element("2", 2)
      }

      "the first list element by id optional with ?" in {
        val result = collector.?("2")
        result.isDefined shouldBe true
        result.get shouldBe Element("2", 1)
      }

      "the first list element by type with !" in {
        val result = collector.!("2")
        result shouldBe Element("2", 1)
      }
    }


    "should throw an exception" - {

      "if there is no element with given id and ! was used" in {
        assertThrows[NoSuchElementException] {
          collector.!("5")
        }
      }
    }
  }

}
