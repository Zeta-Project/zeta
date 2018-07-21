package de.htwg.zeta.codeGenerator

import de.htwg.zeta.codeGenerator.generation.model.ModelGenerator
import de.htwg.zeta.codeGenerator.model.Anchor
import de.htwg.zeta.codeGenerator.model.GeneratedFolder

/**
 * For this to compile. SBT task twirlCompileTemplates needs to be executed first
 *
 */
object KlimaCodeGenerator {

  def generate(anchor: Anchor): GeneratedFolder = {
    GeneratedFolder("klima", Nil, List(
      ModelGenerator.generate(anchor)
    ))
  }
}