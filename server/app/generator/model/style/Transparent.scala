package generator.model.style

/**
 * Created by julian on 29.10.15.
 */
object Transparent extends Color with Transparency {
  def getRGBValue = """transparent"""
  override def createOpacityValue = """0.0"""
}
