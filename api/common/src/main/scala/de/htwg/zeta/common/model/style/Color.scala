package de.htwg.zeta.common.model.style

case class Color(string: String)

object Color {
  def getRGBValue(color: javafx.scene.paint.Color): String = {
    val r = (color.getRed * 255.0).round.toInt
    val g = (color.getGreen * 255.0).round.toInt
    val b = (color.getBlue * 255.0).round.toInt

    s"$r,$g,$b"
  }

  def apply(color: javafx.scene.paint.Color): Color = new Color(getRGBValue(color))
}
