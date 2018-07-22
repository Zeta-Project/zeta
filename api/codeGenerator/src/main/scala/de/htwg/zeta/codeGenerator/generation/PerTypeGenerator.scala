package de.htwg.zeta.codeGenerator.generation

import de.htwg.zeta.codeGenerator.model.AnchorEnum
import de.htwg.zeta.codeGenerator.model.AnchorEnum.Period
import de.htwg.zeta.codeGenerator.model.AnchorEnum.Team
import de.htwg.zeta.codeGenerator.model.AnchorWithEntities
import de.htwg.zeta.codeGenerator.model.Entity
import de.htwg.zeta.codeGenerator.model.GeneratedFile
import de.htwg.zeta.codeGenerator.model.GeneratedFolder

/**
 */
trait PerTypeGenerator {

  protected val packageName: String
  protected lazy val teamPackage: String =  KlimaCodeGenerator.teamConst + packageName.capitalize
  protected lazy val periodPackage: String = KlimaCodeGenerator.periodConst + packageName.capitalize

  protected def buildTypeIndependentFolders(start: AnchorWithEntities): List[GeneratedFolder]

  protected def buildTypeIndependentFiles(start: AnchorWithEntities): List[GeneratedFile]

  protected def buildEntity(comp: Entity, tpe: AnchorEnum, simName: String): GeneratedFile

  def generate(start: AnchorWithEntities): GeneratedFolder = {
    def buildForType(e: AnchorEnum) = {
      val list = e match {
        case AnchorEnum.Team => start.teamEntities
        case AnchorEnum.Period => start.periodEntities
      }
      list.map(c => buildEntity(c, e, start.name))
    }

    val folder = List(
      GeneratedFolder(teamPackage, buildForType(Team), Nil),
      GeneratedFolder(periodPackage, buildForType(Period), Nil)
    ) ++ buildTypeIndependentFolders(start)
    val files = buildTypeIndependentFiles(start)


    GeneratedFolder(packageName, files, folder)
  }

}
