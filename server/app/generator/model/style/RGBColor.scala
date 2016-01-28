package generator.model.style

/**
 * Created by julian on 29.10.15.
 */
class RGBColor(val red: Int, green: Int, blue: Int) extends Color {
  def getRGBValue = "" + red + green + blue
}