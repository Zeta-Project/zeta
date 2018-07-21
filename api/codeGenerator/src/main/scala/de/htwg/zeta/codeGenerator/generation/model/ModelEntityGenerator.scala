package de.htwg.zeta.codeGenerator.generation.model

import de.htwg.zeta.codeGenerator.generation.model.html.EntityTemplate
import de.htwg.zeta.codeGenerator.model.Entity
import de.htwg.zeta.codeGenerator.model.GeneratedFile

object ModelEntityGenerator {
  def generate(entity: Entity): GeneratedFile = {
    val cont = EntityTemplate(entity).body
    GeneratedFile(entity.name, GeneratedFile.scala, cont)
  }

}
