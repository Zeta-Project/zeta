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
    val cont = PeriodEntityTemplate().body
    GeneratedFile("PeriodEntity", GeneratedFile.scala, cont)
  }
  private def generateModel(periodStart: Entity): GeneratedFile = {
    val cont = PeriodModelTemplate(periodStart).body
    GeneratedFile("PeriodModel", GeneratedFile.scala, cont)
  }
  private def generateInput(periodStart: Entity): GeneratedFile = {
    val cont = PeriodInputTemplate(periodStart).body
    GeneratedFile("PeriodInput", GeneratedFile.scala, cont)
  }
  private def generateOutput(periodStart: Entity): GeneratedFile = {
    val cont = PeriodOutputTemplate(periodStart).body
    GeneratedFile("PeriodOutput", GeneratedFile.scala, cont)
  }
}
