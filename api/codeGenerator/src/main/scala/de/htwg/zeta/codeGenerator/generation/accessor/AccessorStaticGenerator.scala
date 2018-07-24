package de.htwg.zeta.codeGenerator.generation.accessor

import de.htwg.zeta.codeGenerator.generation.accesor.html.InputValueAccessorTemplate
import de.htwg.zeta.codeGenerator.generation.accesor.html.MapValueAccessorTemplate
import de.htwg.zeta.codeGenerator.generation.accesor.html.OutputValueAccessorTemplate
import de.htwg.zeta.codeGenerator.generation.accesor.html.ValueAccessorTemplate
import de.htwg.zeta.codeGenerator.model.AnchorEnum
import de.htwg.zeta.codeGenerator.model.AnchorEnum.Period
import de.htwg.zeta.codeGenerator.model.AnchorEnum.Team
import de.htwg.zeta.codeGenerator.model.GeneratedFile
import de.htwg.zeta.codeGenerator.model.GeneratedFolder

object AccessorStaticGenerator {
  val valueAccessorPackage = "valueAccessor"

  def generate(): GeneratedFolder = {
    val perTypeFiles = List(Team, Period).flatMap(start => List(
      generateValueAccessor(start),
      generateInputValueAccessor(start),
      generateOutputValueAccessor(start)
    ))

    val files = generateMapValueAccessor :: perTypeFiles

    GeneratedFolder(valueAccessorPackage, files, Nil)
  }


  private def generateValueAccessor(start: AnchorEnum): GeneratedFile = {
    GeneratedFile(s"${start}ValueAccessor", GeneratedFile.scala, ValueAccessorTemplate(start))
  }
  private def generateInputValueAccessor(start: AnchorEnum): GeneratedFile = {
    GeneratedFile(s"${start}InputValueAccessor", GeneratedFile.scala, InputValueAccessorTemplate(start))
  }
  private def generateOutputValueAccessor(start: AnchorEnum): GeneratedFile = {
    GeneratedFile(s"${start}OutputValueAccessor", GeneratedFile.scala, OutputValueAccessorTemplate(start))
  }

  private def generateMapValueAccessor(): GeneratedFile = {
    GeneratedFile("MapValueAccessor", GeneratedFile.scala, MapValueAccessorTemplate())

  }

}
