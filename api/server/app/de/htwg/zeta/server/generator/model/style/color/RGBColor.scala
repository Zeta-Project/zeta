package de.htwg.zeta.server.generator.model.style.color

sealed class RGBColor private (val red: Int, val green: Int, val blue: Int) extends Color {

  val (r, g, b) = { val form = "%02x"; (form format red, form format green, form format blue) }
  override def getRGBValue: String = s"$r$g$b"
  override def toString: String = "RGBColor (red=" + red + ", green=" + green + ", blue=" + blue + ")"
}

object RGBColor {
  def apply(red: Int, green: Int, blue: Int) = new RGBColor(red, green, blue)
  def apply(t: (Int, Int, Int)) = new RGBColor(t._1, t._2, t._3)
}

