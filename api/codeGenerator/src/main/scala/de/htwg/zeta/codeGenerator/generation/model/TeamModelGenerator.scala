package de.htwg.zeta.codeGenerator.generation.model

import de.htwg.zeta.codeGenerator.generation.model.html.TeamEntityTemplate
import de.htwg.zeta.codeGenerator.generation.model.html.TeamInputTemplate
import de.htwg.zeta.codeGenerator.generation.model.html.TeamModelTemplate
import de.htwg.zeta.codeGenerator.generation.model.html.TeamOutputTemplate
import de.htwg.zeta.codeGenerator.model.Anchor
import de.htwg.zeta.codeGenerator.model.Entity
import de.htwg.zeta.codeGenerator.model.GeneratedFile
import de.htwg.zeta.codeGenerator.model.GeneratedFolder

object TeamModelGenerator {
  def generate(anchor: Anchor): GeneratedFolder = {

    GeneratedFolder("teamEntity",
      List(
        generateEntity(anchor.team),
        generateModel(anchor.team),
        generateInput(anchor.team),
        generateOutput(anchor.team)
      ), Nil)
  }

  private def generateEntity(teamStart: Entity): GeneratedFile = {
    val cont = TeamEntityTemplate().body
    GeneratedFile("TeamEntity", GeneratedFile.scala, cont)
  }
  private def generateModel(teamStart: Entity): GeneratedFile = {
    val cont = TeamModelTemplate(teamStart).body
    GeneratedFile("TeamModel", GeneratedFile.scala, cont)
  }
  private def generateInput(teamStart: Entity): GeneratedFile = {
    val cont = TeamInputTemplate(teamStart).body
    GeneratedFile("TeamInput", GeneratedFile.scala, cont)
  }
  private def generateOutput(teamStart: Entity): GeneratedFile = {
    val cont = TeamOutputTemplate(teamStart).body
    GeneratedFile("TeamOutput", GeneratedFile.scala, cont)
  }
}
