package de.htwg.zeta.codeGenerator.generation.accessor

import de.htwg.zeta.codeGenerator.generation.PerTypeGenerator
import de.htwg.zeta.codeGenerator.generation.accesor.html.AccessorTemplate
import de.htwg.zeta.codeGenerator.generation.accesor.html.BaseAccessorTemplate
import de.htwg.zeta.codeGenerator.model.AnchorEnum
import de.htwg.zeta.codeGenerator.model.AnchorWithEntities
import de.htwg.zeta.codeGenerator.model.Entity
import de.htwg.zeta.codeGenerator.model.GeneratedFile
import de.htwg.zeta.codeGenerator.model.GeneratedFolder

object AccessorGenerator extends PerTypeGenerator {
  override protected val packageName: String = "accessor"
  override protected def buildTypeIndependentFolders(start: AnchorWithEntities): List[GeneratedFolder] = {
    List(AccessorStaticGenerator.generate())
  }

  override protected def buildTypeIndependentFiles(start: AnchorWithEntities): List[GeneratedFile] = {
    val content = BaseAccessorTemplate(start.anchor, teamPackage, periodPackage) _
    val baseAccessor = GeneratedFile("Accessor", GeneratedFile.scala, content)

    List(baseAccessor)
  }

  override protected def buildEntity(entity: Entity, startType: AnchorEnum, simName: String): GeneratedFile = {
    val fileName = s"${entity.name}${startType}Accessor"
    val content = AccessorTemplate(entity, startType, fileName) _
    GeneratedFile(fileName, GeneratedFile.scala, content)
  }
}
