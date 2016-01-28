package generator.model.style

/**
 * Created by julian on 29.10.15.
 */
abstract class Color extends ColorOrGradient with Transparency {
  def createOpacityValue = """1.0"""
}

case object WHITE             extends Color { def getRGBValue = """#ffffff""" }
case object LIGHT_LIGHT_GRAY  extends Color { def getRGBValue = """#e9e9e9""" }
case object LIGHT_GRAY        extends Color { def getRGBValue = """#d3d3d3""" }
case object GRAY              extends Color { def getRGBValue = """#808080""" }
case object DARK_GRAY         extends Color { def getRGBValue = """#a9a9a9""" }
case object BLACK             extends Color { def getRGBValue = """#000000""" }
case object RED               extends Color { def getRGBValue = """#ff0000""" }
case object LIGHT_ORANGE      extends Color { def getRGBValue = """#ffa07a""" }
case object ORANGE            extends Color { def getRGBValue = """#ffa500""" }
case object DARK_ORANGE       extends Color { def getRGBValue = """#ff8c00""" }
case object YELLOW            extends Color { def getRGBValue = """#ffff00""" }
case object GREEN             extends Color { def getRGBValue = """#008000""" }
case object LIGHT_GREEN       extends Color { def getRGBValue = """#90EE90""" }
case object DARK_GREEN        extends Color { def getRGBValue = """#006400""" }
case object CYAN              extends Color { def getRGBValue = """#00ffff""" }
case object LIGHT_BLUE        extends Color { def getRGBValue = """#add8e6""" }
case object BLUE              extends Color { def getRGBValue = """#0000ff""" }
case object DARK_BLUE         extends Color { def getRGBValue = """#00008b""" }
