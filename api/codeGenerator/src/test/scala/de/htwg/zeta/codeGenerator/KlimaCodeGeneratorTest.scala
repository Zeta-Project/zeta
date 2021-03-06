package de.htwg.zeta.codeGenerator

import de.htwg.zeta.codeGenerator.generation.KlimaCodeGenerator
import de.htwg.zeta.codeGenerator.model.Anchor
import de.htwg.zeta.codeGenerator.model.Entity
import de.htwg.zeta.codeGenerator.model.Link
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class KlimaCodeGeneratorTest extends AnyFreeSpec with Matchers {

  private val generatorToTest = KlimaCodeGenerator

  "A KlimaCodeGenerator" - {
    "should generate" - {

      "an Example" - {
        // Instance
        val bed = Entity("Bett", Nil, Nil, Nil, Nil, Nil, Nil)
        val bedLink = Link("bedLink", bed)
        val dep = Entity("Department", Nil, Nil, Nil, List(bedLink), Nil, Nil)
        val depChLink = Link("depChLink", dep)
        val depUchLink = Link("depUchLink", dep)
        val myEntity = Entity("Krankenhaus", Nil, Nil, Nil, List(depChLink, depUchLink), Nil, Nil)
        val periodEntity = Entity("PeriodEntity", Nil, Nil, Nil, Nil, Nil, Nil)
        val anchor = Anchor("klima", myEntity, periodEntity)
        val klimaCodeGen = KlimaCodeGenerator.generate(anchor, "de", "htwg")
        klimaCodeGen.name shouldBe "de"
        klimaCodeGen.children.nonEmpty shouldBe true
      }

    }
  }
}
