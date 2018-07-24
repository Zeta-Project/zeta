package de.htwg.zeta.codeGenerator.generation.model

import de.htwg.zeta.codeGenerator.generation.model.html.PeriodEntityTemplate
import de.htwg.zeta.codeGenerator.generation.model.html.PeriodInputTemplate
import de.htwg.zeta.codeGenerator.generation.model.html.PeriodModelTemplate
import de.htwg.zeta.codeGenerator.generation.model.html.PeriodOutputTemplate
import de.htwg.zeta.codeGenerator.model.Anchor
import de.htwg.zeta.codeGenerator.model.Entity
import de.htwg.zeta.codeGenerator.model.GeneratedFile
import de.htwg.zeta.codeGenerator.model.GeneratedFolder

object PeriodModelGenerator {
  def generate(anchor: Anchor): GeneratedFolder = {

    GeneratedFolder("periodEntity",
      List(
        generateEntity(anchor.period),
        generateModel(anchor.period),
        generateInput(anchor.period),
        generateOutput(anchor.period)
      ), Nil)
  }

  private def generateEntity(periodStart: Entity): GeneratedFile = {
    GeneratedFile("PeriodEntity", GeneratedFile.scala, PeriodEntityTemplate())
  }
  private def generateModel(periodStart: Entity): GeneratedFile = {
    GeneratedFile("PeriodModel", GeneratedFile.scala, PeriodModelTemplate(periodStart))
  }
  private def generateInput(periodStart: Entity): GeneratedFile = {
    GeneratedFile("PeriodInput", GeneratedFile.scala, PeriodInputTemplate(periodStart))
  }
  private def generateOutput(periodStart: Entity): GeneratedFile = {
    GeneratedFile("PeriodOutput", GeneratedFile.scala, PeriodOutputTemplate(periodStart))
  }
}
