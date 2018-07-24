package de.htwg.zeta.codeGenerator.generation.model

import de.htwg.zeta.codeGenerator.generation.model.html.GameEntityTemplate
import de.htwg.zeta.codeGenerator.model.GeneratedFile
import de.htwg.zeta.codeGenerator.model.GeneratedFolder

object GameModelGenerator {
  def generate(): GeneratedFolder = {
    val cont = GameEntityTemplate() _
    val file = GeneratedFile("GameEntity", GeneratedFile.scala, cont)
    GeneratedFolder("gameEntity", List(file), Nil)
  }
}
