package de.htwg.zeta.codeGenerator.generation

import de.htwg.zeta.codeGenerator.generation.accessor.AccessorGenerator
import de.htwg.zeta.codeGenerator.generation.model.ModelGenerator
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

  def generate(anchor: Anchor): GeneratedFolder = {
    val awe = AnchorWithEntities(
      anchor,
      EntityCollector.collectAllEntities(anchor.team),
      EntityCollector.collectAllEntities(anchor.period),
      EntityCollector.collectAllEntities(anchor.team, anchor.period)
    )
    GeneratedFolder(anchor.name, Nil, List(
      AccessorGenerator.generate(awe),
      ModelGenerator.generate(awe)
    ))
  }
}