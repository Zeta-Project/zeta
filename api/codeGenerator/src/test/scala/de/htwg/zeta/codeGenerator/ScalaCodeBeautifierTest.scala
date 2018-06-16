package de.htwg.zeta.codeGenerator

import org.scalatest.FreeSpec
import org.scalatest.Matchers

class ScalaCodeBeautifierTest extends FreeSpec with Matchers {

  "A scala code beautifier should" - {
    "beautify a case class string" in {
      ScalaCodeBeautifier.format("Test.scala", "\n\ncase class Test(t: \nString, f:\nString)") shouldBe
        "case class Test(t: String, f: String)"
    }
  }

}
