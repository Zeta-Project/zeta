package de.htwg.zeta.parser.check

import de.htwg.zeta.parser.check.Check.Id
import org.scalatest.Inside
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

//noinspection ScalaStyle
class FindUndefinedElementsTest extends AnyFreeSpec with Matchers with Inside {

  val elementToId: Element => Id = element => element.name
  val getParentIds: Element => List[Id] = element => element.parents.toList
  val findUndefinedParents = new FindUndefinedElements[Element](elementToId, getParentIds)

  "Check for undefined parents will result in" - {

    "an empty result list when" - {

      "an empty list is passed" in {
        val elements = List.empty
        val undefinedParents = findUndefinedParents(elements)
        undefinedParents shouldBe empty
      }

      "the there are no parents specified" in {
        val elements = List(
          Element("e1"),
          Element("e2"),
          Element("e3")
        )
        val undefinedParents = findUndefinedParents(elements)
        undefinedParents shouldBe empty
      }

      "all referenced parents are defined" in {
        val elements = List(
          Element("child", parents = "parent1", "parent2"),
          Element("parent1"),
          Element("parent2", parents = "root"),
          Element("root")
        )
        val undefinedParents = findUndefinedParents(elements)
        undefinedParents shouldBe empty
      }
    }

    "a non empty result list when" - {

      "an element references an undefined parent" in {
        val elements = List(
          Element("child", parents = "undefinedParent")
        )
        val undefinedParents = findUndefinedParents(elements)
        undefinedParents should have size 1
        undefinedParents should contain("undefinedParent")
      }

      "an element references multiple undefined parents" in {
        val elements = List(
          Element("child", parents = "undefinedParent1", "undefinedParent2", "undefinedParent3")
        )
        val undefinedParents = findUndefinedParents(elements)
        undefinedParents should have size 3
        undefinedParents should contain("undefinedParent1")
        undefinedParents should contain("undefinedParent2")
        undefinedParents should contain("undefinedParent3")
      }

      "several elements reference the same undefined parent" in {
        val elements = List(
          Element("child1", parents = "undefinedParent"),
          Element("child2", parents = "undefinedParent"),
          Element("child3", parents = "undefinedParent")
        )
        val undefinedParents = findUndefinedParents(elements)
        undefinedParents should have size 1
        undefinedParents should contain("undefinedParent")
      }

      "several elements reference different undefined parents" in {
        val elements = List(
          Element("child1", parents = "undefinedParent1"),
          Element("child2", parents = "undefinedParent1"),
          Element("child3", parents = "undefinedParent2")
        )
        val undefinedParents = findUndefinedParents(elements)
        undefinedParents should have size 2
        undefinedParents should contain("undefinedParent1")
        undefinedParents should contain("undefinedParent2")
      }
    }

  }


}

