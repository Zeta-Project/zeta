package de.htwg.zeta.parser.check

import de.htwg.zeta.parser.check.Check.Id
import org.scalatest.FreeSpec
import org.scalatest.Inside
import org.scalatest.Matchers

//noinspection ScalaStyle
class FindInvalidPointersTest extends FreeSpec with Matchers with Inside {

  val elementToId: Element => Id = element => element.name
  val availableIds: List[String] = List("t1", "t2", "t3", "t4")
  val findInvalidPointers = new FindInvalidPointers[Element](elementToId, availableIds)

  "Check for invalid ids will result in" - {

    "an empty list when" - {

      "the pointer list is empty" in {
        val invalidIds = findInvalidPointers(List.empty)
        invalidIds shouldBe empty
      }

      "the element list contains a single id" in {
        val ids = List(Element("t1"))
        val invalidIds = findInvalidPointers(ids)
        invalidIds shouldBe empty
      }

      "the element list contains valid ids" in {
        val ids = List(Element("t1"), Element("t2"), Element("t3"), Element("t4"))
        val invalidIds = findInvalidPointers(ids)
        invalidIds shouldBe empty
      }

      "the element list contains valid ids (with duplicates)" in {
        val ids = List(Element("t1"), Element("t2"), Element("t1"), Element("t2"))
        val invalidIds = findInvalidPointers(ids)
        invalidIds shouldBe empty
      }

    }

    "a non empty list of invalid ids when" - {

      "a single element with that id is not found" in {
        val ids = List(Element("t5"), Element("t2"))
        val invalidIds = findInvalidPointers(ids)
        invalidIds should have size 1
        invalidIds should contain("t5")
      }

      "multiple elements with that id is not found" in {
        val ids = List(Element("t5"), Element("t7"))
        val invalidIds = findInvalidPointers(ids)
        invalidIds should have size 2
        invalidIds should contain("t5")
        invalidIds should contain("t7")
      }

    }

  }


}
