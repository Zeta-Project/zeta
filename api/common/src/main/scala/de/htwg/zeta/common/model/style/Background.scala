package de.htwg.zeta.common.model.style

case class Background(color: Color)

object Background {
  val defaultColor: Color = Color(255, 255, 255)

  val defaultBackground: Background = Background(
    defaultColor
  )
}
