package de.htwg.zeta.codeGenerator

import de.htwg.zeta.codeGenerator.generation.KlimaCodeGenerator
import de.htwg.zeta.codeGenerator.model.Anchor
import de.htwg.zeta.codeGenerator.model.Entity
import de.htwg.zeta.codeGenerator.model.Link
import org.scalatest.FreeSpec

class KlimaCodeGeneratorTest extends FreeSpec {

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
        println(KlimaCodeGenerator.generate(anchor))
      }

    }
  }
}
