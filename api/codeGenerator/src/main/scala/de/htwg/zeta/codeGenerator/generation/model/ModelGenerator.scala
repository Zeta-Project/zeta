package de.htwg.zeta.codeGenerator.generation.model

import de.htwg.zeta.codeGenerator.model.Anchor
import de.htwg.zeta.codeGenerator.model.AnchorWithEntities
import de.htwg.zeta.codeGenerator.model.Entity
import de.htwg.zeta.codeGenerator.model.GeneratedFolder

/**
 */
object ModelGenerator extends App {

  def generate(anchor: AnchorWithEntities): GeneratedFolder = {
    createModelPackage(anchor.anchor, anchor.allEntities)
  }

  private def createModelPackage(start: Anchor, comps: List[Entity]): GeneratedFolder = {
    val components = GeneratedFolder("entity", comps.map(cmp => ModelEntityGenerator.generate(cmp)), Nil)
    val periodEntity = PeriodModelGenerator.generate(start)
    val teamEntity = TeamModelGenerator.generate(start)
    val gameEntity = GameModelGenerator.generate()
    GeneratedFolder("model", Nil, List(components, periodEntity, teamEntity, gameEntity))
  }
}
