package de.htwg.zeta.codeGenerator.generation.framework

import de.htwg.zeta.codeGenerator.generation.framework.html.DependentValueTemplate
import de.htwg.zeta.codeGenerator.model.GeneratedFile
import de.htwg.zeta.codeGenerator.model.GeneratedFolder

object DependentValueBuilder {
  val folderName = "dependentValue"
  def generate(): GeneratedFolder = {
    val content = DependentValueTemplate(folderName).body
    val file = GeneratedFile("DependentValue", GeneratedFile.scala, content)
    GeneratedFolder.wrapFiles(folderName)(file)
  }

}
