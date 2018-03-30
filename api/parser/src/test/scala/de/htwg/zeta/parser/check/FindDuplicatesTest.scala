package de.htwg.zeta.parser.check

import de.htwg.zeta.parser.check.Check.Id
import org.scalatest.FreeSpec
import org.scalatest.Inside
import org.scalatest.Matchers

//noinspection ScalaStyle
class FindDuplicatesTest extends FreeSpec with Matchers with Inside {

  val elementToId: Element => Id = element => element.name
  val findDuplicates = new FindDuplicates[Element](elementToId)

  "Check for duplicates will result in" - {

    "an empty list when" - {

      "the element list is empty" in {
        val duplicates = findDuplicates(List.empty)
        duplicates shouldBe empty
      }

      "the element list contains a single element" in {
        val elements = List(
          Element("e1")
        )
        val duplicates = findDuplicates(elements)
        duplicates shouldBe empty
      }

      "the element list contains no duplicates" in {
        val elements = List(
          Element("e1"),
          Element("e2"),
          Element("e3")
        )
        val duplicates = findDuplicates(elements)
        duplicates shouldBe empty
      }

    }

    "a non empty list of duplicates when" - {

      "the element list contains an element twice" in {
        val elements = List(
          Element("e1"), Element("e1")
        )
        val duplicates = findDuplicates(elements)
        duplicates should have size 1
        duplicates should contain("e1")
      }

      "the element list contains an element multiple times" in {
        val elements = List(Element("e1"), Element("e1"), Element("e1"))
        val duplicates = findDuplicates(elements)
        duplicates should have size 1
        duplicates should contain("e1")
      }

      "the element list contains multiple elements multiple times" in {
        val elements = List(
          Element("e1"),
          Element("e2"), Element("e2"),
          Element("e3"), Element("e3"), Element("e3")
        )
        val duplicates = findDuplicates(elements)
        duplicates should have size 2
        duplicates should contain("e2")
        duplicates should contain("e3")
      }

    }

  }



}
