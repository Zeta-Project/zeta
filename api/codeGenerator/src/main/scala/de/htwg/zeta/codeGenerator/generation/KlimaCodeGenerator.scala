package de.htwg.zeta.codeGenerator.generation

import de.htwg.zeta.codeGenerator.generation.accessor.AccessorGenerator
import de.htwg.zeta.codeGenerator.generation.defaultValue.DefaultValueGenerator
import de.htwg.zeta.codeGenerator.generation.framework.DependentValueBuilder
import de.htwg.zeta.codeGenerator.generation.model.ModelGenerator
import de.htwg.zeta.codeGenerator.generation.transition.TransitionGenerator
import de.htwg.zeta.codeGenerator.model.Anchor
import de.htwg.zeta.codeGenerator.model.AnchorWithEntities
import de.htwg.zeta.codeGenerator.model.GeneratedFolder

/**
 * For this to compile. SBT task twirlCompileTemplates needs to be executed first
 *
 */
object KlimaCodeGenerator {
  val periodConst = "period"
  val teamConst = "team"


  private def generateFramework(): GeneratedFolder = {
    GeneratedFolder.wrapInFolder("framework")(
      DependentValueBuilder.generate()
    )
  }

  private def generateAnchor(anchor: Anchor): GeneratedFolder = {
    val awe = AnchorWithEntities(
      anchor,
      EntityCollector.collectAllEntities(anchor.team),
      EntityCollector.collectAllEntities(anchor.period),
      EntityCollector.collectAllEntities(anchor.team, anchor.period)
    )

    GeneratedFolder.wrapInFolder("generated", anchor.name)(
      AccessorGenerator.generate(awe),
      TransitionGenerator.generate(awe),
      DefaultValueGenerator.generate(awe),
      ModelGenerator.generate(awe)
    )
  }

  def generate(anchor: Anchor, prefixHead: String, prefixTail: String*): GeneratedFolder = {
    GeneratedFolder.wrapInFolder(prefixHead, prefixTail: _*)(
      generateAnchor(anchor),
      generateFramework()
    )
  }
}