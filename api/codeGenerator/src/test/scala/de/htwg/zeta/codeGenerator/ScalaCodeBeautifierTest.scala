package de.htwg.zeta.codeGenerator

import org.scalatest.matchers.should.Matchers
import org.scalatest.freespec.AnyFreeSpec

class ScalaCodeBeautifierTest extends AnyFreeSpec with Matchers {

  "A scala code beautifier should" - {
    "beautify a case class string" in {
      ScalaCodeBeautifier.format("Test.scala",
        """
         |
         |case class Test(t: String,
         |
         |f: String)
        """.stripMargin) shouldBe "case class Test(\n  t: String,\n  f: String\n)"
    }
    "return the source string in case of parse failure" in {
      val input = "\ncase wrong syntax"
      ScalaCodeBeautifier.format("WrongTest.scala", s"\n$input") shouldBe input
    }
  }

}
