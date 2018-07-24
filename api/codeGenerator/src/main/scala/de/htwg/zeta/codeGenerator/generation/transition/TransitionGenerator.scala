package de.htwg.zeta.codeGenerator.generation.transition

import de.htwg.zeta.codeGenerator.generation.PerTypeGenerator
import de.htwg.zeta.codeGenerator.generation.transition.html.GameTransitionTemplate
import de.htwg.zeta.codeGenerator.generation.transition.html.PeriodTransitionResourcesTemplate
import de.htwg.zeta.codeGenerator.generation.transition.html.PeriodTransitionTemplate
import de.htwg.zeta.codeGenerator.generation.transition.html.TeamTransitionResourcesTemplate
import de.htwg.zeta.codeGenerator.generation.transition.html.TeamTransitionTemplate
import de.htwg.zeta.codeGenerator.generation.transition.html.TransitionTemplate
import de.htwg.zeta.codeGenerator.model.AnchorEnum
import de.htwg.zeta.codeGenerator.model.AnchorWithEntities
import de.htwg.zeta.codeGenerator.model.Entity
import de.htwg.zeta.codeGenerator.model.GeneratedFile
import de.htwg.zeta.codeGenerator.model.GeneratedFolder

object TransitionGenerator extends PerTypeGenerator {
  override protected val packageName: String = "transition"
  override protected def buildTypeIndependentFolders(start: AnchorWithEntities): List[GeneratedFolder] = {
    // TODO do i use binding or not
    // List(BindingFactoryGenerator.buildFactoryFolder(start))
    Nil
  }

  override protected def buildTypeIndependentFiles(start: AnchorWithEntities): List[GeneratedFile] = List(
    GeneratedFile.scalaFile("GameTransition", GameTransitionTemplate(start.anchor)),
    GeneratedFile.scalaFile("PeriodTransition", PeriodTransitionTemplate(start.anchor)),
    GeneratedFile.scalaFile("TeamTransition", TeamTransitionTemplate(start.anchor)),
    GeneratedFile.scalaFile("TeamTransitionResources", TeamTransitionResourcesTemplate()),
    GeneratedFile.scalaFile("PeriodTransitionResources", PeriodTransitionResourcesTemplate())
  )

  override protected def buildEntity(entity: Entity, tpe: AnchorEnum, simName: String): GeneratedFile = {
    GeneratedFile.scalaFile(s"${entity.name}${tpe}Transition", TransitionTemplate(entity, tpe))
  }
}
