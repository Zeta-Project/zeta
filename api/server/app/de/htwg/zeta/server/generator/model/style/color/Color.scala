package de.htwg.zeta.server.generator.model.style.color

abstract class Color extends ColorOrGradient with ColorWithTransparency

abstract class ColorConstant extends Color {
  def getRGBValue: String
}
object ColorConstant {
  val knownColors: Map[String, Color] = Map(
    "white" -> WHITE,
    "light-light-gray" -> LIGHT_LIGHT_GRAY,
    "light-gray" -> LIGHT_GRAY,
    "gray" -> GRAY,
    "black" -> BLACK,
    "red" -> RED,
    "light-orange" -> LIGHT_ORANGE,
    "orange" -> ORANGE,
    "dark-orange" -> DARK_ORANGE,
    "yellow" -> YELLOW,
    "green" -> GREEN,
    "light-green" -> LIGHT_GREEN,
    "dark-green" -> DARK_GREEN,
    "cyan" -> CYAN,
    "light-blue" -> LIGHT_BLUE,
    "blue" -> BLUE,
    "dark-blue" -> DARK_BLUE,
    "transparent" -> Transparent
  )
}

case object WHITE extends ColorConstant { override def getRGBValue = """#ffffff""" }
case object LIGHT_LIGHT_GRAY extends ColorConstant { override def getRGBValue = """#e9e9e9""" }
case object LIGHT_GRAY extends ColorConstant { override def getRGBValue = """#d3d3d3""" }
case object GRAY extends ColorConstant { override def getRGBValue = """#808080""" }
case object DARK_GRAY extends ColorConstant { override def getRGBValue = """#a9a9a9""" }
case object BLACK extends ColorConstant { override def getRGBValue = """#000000""" }
case object RED extends ColorConstant { override def getRGBValue = """#ff0000""" }
case object LIGHT_ORANGE extends ColorConstant { override def getRGBValue = """#ffa07a""" }
case object ORANGE extends ColorConstant { override def getRGBValue = """#ffa500""" }
case object DARK_ORANGE extends ColorConstant { override def getRGBValue = """#ff8c00""" }
case object YELLOW extends ColorConstant { override def getRGBValue = """#ffff00""" }
case object GREEN extends ColorConstant { override def getRGBValue = """#008000""" }
case object LIGHT_GREEN extends ColorConstant { override def getRGBValue = """#90EE90""" }
case object DARK_GREEN extends ColorConstant { override def getRGBValue = """#006400""" }
case object CYAN extends ColorConstant { override def getRGBValue = """#00ffff""" }
case object LIGHT_BLUE extends ColorConstant { override def getRGBValue = """#add8e6""" }
case object BLUE extends ColorConstant { override def getRGBValue = """#0000ff""" }
case object DARK_BLUE extends ColorConstant { override def getRGBValue = """#00008b""" }

