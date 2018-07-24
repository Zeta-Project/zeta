package de.htwg.zeta.codeGenerator.generation.defaultValue

import de.htwg.zeta.codeGenerator.generation.PerTypeGenerator
import de.htwg.zeta.codeGenerator.generation.defaultValue.html.DefaultValueTemplate
import de.htwg.zeta.codeGenerator.generation.defaultValue.html.PeriodDefaultValueTemplate
import de.htwg.zeta.codeGenerator.generation.defaultValue.html.TeamDefaultValueTemplate
import de.htwg.zeta.codeGenerator.model.AnchorEnum
import de.htwg.zeta.codeGenerator.model.AnchorWithEntities
import de.htwg.zeta.codeGenerator.model.Entity
import de.htwg.zeta.codeGenerator.model.GeneratedFile
import de.htwg.zeta.codeGenerator.model.GeneratedFolder

object DefaultValueGenerator extends PerTypeGenerator {
  override protected val packageName: String = "defaultValue"
  override protected def buildTypeIndependentFolders(start: AnchorWithEntities): List[GeneratedFolder] = Nil


  override protected def buildTypeIndependentFiles(start: AnchorWithEntities): List[GeneratedFile] = {
    List(
      GeneratedFile.scalaFile("PeriodDefaultValue", PeriodDefaultValueTemplate(start.period.name)),
      GeneratedFile.scalaFile("TeamDefaultValue", TeamDefaultValueTemplate(start.team.name))
    )
  }

  override protected def buildEntity(entity: Entity, tpe: AnchorEnum, simName: String): GeneratedFile = {
    val defValue = tpe.name + "DefaultValue"
    GeneratedFile.scalaFile(s"${entity.name}$defValue", DefaultValueTemplate(entity, defValue))
  }
}
