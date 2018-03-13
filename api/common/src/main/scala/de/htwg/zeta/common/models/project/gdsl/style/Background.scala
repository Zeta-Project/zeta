package de.htwg.zeta.common.models.project.gdsl.style

case class Background(color: Color)

object Background {
  val defaultColor: Color = Color(255, 255, 255)

  val defaultBackground: Background = Background(
    defaultColor
  )
}
