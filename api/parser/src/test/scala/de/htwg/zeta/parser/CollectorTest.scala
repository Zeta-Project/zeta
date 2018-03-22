package de.htwg.zeta.parser

import org.scalatest.{FreeSpec, Matchers}

//noinspection ScalaStyle
class CollectorTest extends FreeSpec with Matchers {

  trait Attribute

  case class Size(width: Int, height: Int) extends Attribute

  case class Multiline(multiline: Boolean) extends Attribute

  case class UnusedAttribute() extends Attribute

  private val list = List(Size(1, 1), Size(2, 2), Multiline(false), Size(3, 3))

  "A collector" - {

    "can collect" - {

      "all list elements by type with *" in {
        val attrs = Collector(list)
        attrs.*[Size] shouldBe List(
          Size(1, 1),
          Size(2, 2),
          Size(3, 3)
        )
        attrs.*[Multiline] shouldBe List(
          Multiline(false)
        )
        attrs.*[UnusedAttribute] shouldBe Nil
      }

      "the first list element by type optional with ?" in {
        val attrs = Collector(list)
        attrs.?[Size] shouldBe Some(Size(1, 1))
        attrs.?[Multiline] shouldBe Some(Multiline(false))
        attrs.?[UnusedAttribute] shouldBe None
      }

      "the first list element by type with !" in {
        val attrs = Collector(list)
        attrs.![Size] shouldBe Size(1, 1)
        attrs.![Multiline] shouldBe Multiline(false)
        assertThrows[NoSuchElementException] {
          attrs.![UnusedAttribute]
        }
      }
    }
  }

}
