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
    GeneratedFile("TeamEntity", GeneratedFile.scala, TeamEntityTemplate())
  }
  private def generateModel(teamStart: Entity): GeneratedFile = {
    GeneratedFile("TeamModel", GeneratedFile.scala, TeamModelTemplate(teamStart))
  }
  private def generateInput(teamStart: Entity): GeneratedFile = {
    GeneratedFile("TeamInput", GeneratedFile.scala, TeamInputTemplate(teamStart))
  }
  private def generateOutput(teamStart: Entity): GeneratedFile = {
    GeneratedFile("TeamOutput", GeneratedFile.scala, TeamOutputTemplate(teamStart))
  }
}
