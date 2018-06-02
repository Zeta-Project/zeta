package de.htwg.zeta.codeGenerator

import org.scalatest.FreeSpec

class KlimaCodeGeneratorTest extends FreeSpec {

  private val generatorToTest = KlimaCodeGenerator

  "A KlimaCodeGenerator" - {
    "should generate" - {
      "a Value as ModelClass" in {
        println(generatorToTest.generateValue())
      }
      "a Link as ModelClass" in {
        println(generatorToTest.generateLink())
      }
      "a MapLink as ModelClass" in {
        println(generatorToTest.generateMapLink())
      }
      "a ReferenceLink as ModelClass" in {
        println(generatorToTest.generateReferenceLink())
      }
      "a Entity as ModelClass" in {
        println(generatorToTest.generateEntity())
      }
      "a Anchor as ModelClass" in {
        println(generatorToTest.generateAnchor())
      }
    }
  }
}
