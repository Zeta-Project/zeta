package de.htwg.zeta.codeGenerator

import org.scalatest.FreeSpec
import org.scalatest.Matchers
import scalariform.formatter.ScalaFormatter

class ScalaCodeBeautifierTest extends FreeSpec with Matchers {

  "A scala code beautifier should" - {
    "beautify a case class string" in {
      ScalaFormatter.format("case class Test(t: \nString, f:\nString)") shouldBe "case class Test(t: String, f: String)"
    }
  }

}
