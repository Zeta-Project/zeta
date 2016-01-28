package generator.model.style

/**
 * Created by julian on 29.10.15.
 */
abstract class ColorOrGradient {
  /** getRGBValue is createColorValue from StyleGenerator.xtend*/
  def getRGBValue: String
  /** method instead of function from StyleGenerator.xtend*/
  def createOpacityValue: String
}
