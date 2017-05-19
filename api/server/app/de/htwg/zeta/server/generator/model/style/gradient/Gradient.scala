package de.htwg.zeta.server.generator.model.style.gradient

import de.htwg.zeta.server.generator.model.style.StyleContainerElement
import de.htwg.zeta.server.generator.model.style.color.ColorOrGradient

class Gradient(
    val name: String,
    val description: Option[String],
    val area: List[GradientColorArea]
) extends StyleContainerElement with ColorOrGradient {
  def getRGBValue = ""
}

object Gradient {
  def apply(id: String, description: Option[String], areas: List[GradientColorArea]) = {
    new Gradient(id, description, areas)
  }
}
