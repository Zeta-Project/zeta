package de.htwg.zeta.common.models.project.gdsl.style

case class Background(color: Color)

object Background {
  val defaultColor: Color = Color.defaultColor

  val defaultBackground: Background = Background(
    defaultColor
  )
}
